import json
import os
import urllib.request

# Configuraciones por variables de entorno
LOGIN_URL = os.environ.get('LOGIN_URL')
LOGIN_USUARIO = os.environ.get('LOGIN_USUARIO')
LOGIN_CONTRASENA = os.environ.get('LOGIN_CONTRASENA')
ACTUALIZAR_SOLICITUD_URL = os.environ.get('ACTUALIZAR_SOLICITUD_URL')

def obtener_token():
    try:
        print("Realizando login para obtener token...")
        data = json.dumps({
            'correoElectronico': LOGIN_USUARIO,
            'contrasena': LOGIN_CONTRASENA
        }).encode('utf-8')

        req = urllib.request.Request(
            LOGIN_URL,
            data=data,
            headers={'Content-Type': 'application/json'},
            method='POST'
        )

        with urllib.request.urlopen(req, timeout=10) as response:
            response_data = response.read()
            response_json = json.loads(response_data)
            token = response_json.get('token')
            if not token:
                raise Exception("No se encontró 'token' en la respuesta.")
            print("Token obtenido exitosamente.")
            return token
    except Exception as e:
        print(f"Error al obtener token: {str(e)}")
        raise

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
            return "APROBADA"

    except Exception as e:
        print(f"Error al validar la capacidad de endeudamiento: {str(e)}")
        raise

def consumir_api(token, estadoFinal, idSolicitud):
    try:
        print(f"Consumiendo endpoint para actualizar estado")

        data = json.dumps({
            'id': idSolicitud,
            'estado': estadoFinal
        }).encode('utf-8')

        req = urllib.request.Request(
            ACTUALIZAR_SOLICITUD_URL,
            data=data,
            headers={
                'Authorization': f'Bearer {token}',
                'Content-Type': 'application/json'
            },
            method='POST'
        )

        with urllib.request.urlopen(req, timeout=10) as response:
            response_data = response.read()
            response_json = json.loads(response_data)
            print(f"API llamada con éxito. Código: {response.status}")
            return response_json
    except Exception as e:
        print(f"Error al consumir endpoint para actualizar estado solicitud: {str(e)}")
        raise

def lambda_handler(event, context):
    try:
        token = obtener_token()

        for record in event.get('Records', []):
            try:
                print("Procesando mensaje de SQS...")
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

                resultado = consumir_api(token, estado, idSolicitud)
                print(f"Resultado del API: {resultado}")

            except json.JSONDecodeError as e:
                print(f"Error decodificando JSON: {str(e)}")
            except Exception as e:
                print(f"Error procesando un mensaje: {str(e)}")

    except Exception as e:
        print(f"Error general en la ejecución de Lambda: {str(e)}")
        raise
