import json
import os
import boto3
import uuid

# Configuraciones por variables de entorno
sqs = boto3.client('sqs')
DESTINO_SQS_URL = os.environ.get('DESTINO_SQS_URL')  # ejemplo: https://sqs.us-east-2.amazonaws.com/123456789012/RespuestaEstado.fifo


def validar_endeudamiento(deudaTotalMensualActual, plazoNuevo, montoNuevo, tasaInteres, salario):
    try:
        print(f"Validando capacidad de endeudamiento")
        capacidadEndeudamiento = salario * 0.35
        tasa_mensual = (tasaInteres / 100) / 12

        if tasa_mensual == 0:
            cuotaNueva = montoNuevo / plazoNuevo
        else:
            cuotaNueva = montoNuevo * (
                (tasa_mensual * (1 + tasa_mensual) ** plazoNuevo) /
                ((1 + tasa_mensual) ** plazoNuevo - 1)
            )

        mensualidadFutura = cuotaNueva + deudaTotalMensualActual

        print(f"cuotaNueva: {round(cuotaNueva, 2)}")
        print(f"mensualidadFutura: {round(mensualidadFutura, 2)}")
        print(f"capacidadEndeudamiento: {round(capacidadEndeudamiento, 2)}")

        if mensualidadFutura > capacidadEndeudamiento:
            return "RECHAZADA"
        else:
            return "APROBADO"

    except Exception as e:
        print(f"Error al validar la capacidad de endeudamiento: {str(e)}")
        raise

def enviar_a_cola_destino(idSolicitud, estado):
    try:
        response = sqs.send_message(
            QueueUrl=DESTINO_SQS_URL,
            MessageBody=json.dumps({
                "id": str(idSolicitud),
                "estado": estado
            }),
            MessageGroupId="solicitudRespuesta",
            MessageDeduplicationId=str(uuid.uuid4())
        )
        print(f"Mensaje enviado a la cola de destino. MessageId: {response['MessageId']}")
    except Exception as e:
        print(f"Error enviando a la cola destino: {str(e)}")

def lambda_handler(event, context):
    try:

        for record in event.get('Records', []):
            try:
                print("Procesando mensaje de SQS...")
                print(f"Raw record['body']: {record['body']}")
                body = json.loads(record['body'])
                print(f"Contenido del mensaje: {body}")

                deudaTotalMensualActual = float(body.get('deudaTotalMensualSolicitudesAprobadas') or 0)
                plazoNuevo = int(body.get('plazo') or 0)
                idSolicitud = int(body.get('id') or 0)
                montoNuevo = float(body.get('monto') or 0)
                salario = float(body.get('solicitante', {}).get('salarioBase', 0))
                tasaInteres = float(body.get('tasaInteres') or body.get('tipoPrestamo', {}).get('tasaInteres', 0))

                estado = validar_endeudamiento(
                    deudaTotalMensualActual, plazoNuevo, montoNuevo, tasaInteres, salario
                )

                print(f"Resultado de validación endeudamiento: {estado}")

                enviar_a_cola_destino(idSolicitud, estado)

            except json.JSONDecodeError as e:
                print(f"Error decodificando JSON: {str(e)}")
            except Exception as e:
                print(f"Error procesando un mensaje: {str(e)}")

    except Exception as e:
        print(f"Error general en la ejecución de Lambda: {str(e)}")
        raise
