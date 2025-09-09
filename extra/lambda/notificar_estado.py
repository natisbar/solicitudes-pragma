import json
import os
import smtplib
from email.mime.text import MIMEText

# Variables de entorno para SMTP
SMTP_HOST = os.environ.get('SMTP_HOST', 'smtp.gmail.com')
SMTP_PORT = int(os.environ.get('SMTP_PORT', 587))
SMTP_USER = os.environ.get('SMTP_USER')
SMTP_PASS = os.environ.get('SMTP_PASS')

FROM_EMAIL = SMTP_USER

def lambda_handler(event, context):
    for record in event['Records']:
        try:
            print("Procesando nuevo mensaje desde SQS...")
            body = json.loads(record['body'])
            print(f"Contenido del mensaje: {body}")

            idSolicitud = body.get('id') or (body.get('id', {}) or {}).get('id')
            correo = body.get('correo') or (body.get('solicitante', {}) or {}).get('correoElectronico')
            nombres = body.get('nombres') or (body.get('solicitante', {}) or {}).get('nombres', '')
            apellidos = body.get('apellidos') or (body.get('solicitante', {}) or {}).get('apellidos', '')
            estado = body.get('estado') or ''
            monto = body.get('monto') or ''
            plazo = body.get('plazo') or ''

            if correo:
                asunto = 'Resultado de solicitud de crédito'
                mensaje = f"Hola señor(a) {nombres} {apellidos}, su solicitud de prestamo (#{idSolicitud}) por un monto de {monto} y con un plazo de {plazo} meses, ha sido '{estado}'."

                # Crear el mensaje MIME
                msg = MIMEText(mensaje)
                msg['Subject'] = asunto
                msg['From'] = FROM_EMAIL
                msg['To'] = correo

                # Conectar y enviar correo vía SMTP
                with smtplib.SMTP(SMTP_HOST, SMTP_PORT) as server:
                    server.starttls()  # activar TLS
                    server.login(SMTP_USER, SMTP_PASS)
                    server.sendmail(FROM_EMAIL, [correo], msg.as_string())

                print(f"Correo enviado correctamente a {correo}.")
            else:
                print("Campo 'correo' no encontrado en el mensaje.")

        except json.JSONDecodeError as e:
            print(f"Error decodificando JSON: {str(e)}")
        except Exception as e:
            print(f"Error procesando mensaje: {str(e)}")
