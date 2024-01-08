# Arquitectura Hexagonal con Microservicios REST y gRPC

## Descripción del Proyecto
Este repositorio HexagonalArchitecture es un ejemplo de proyecto open-source que implementa una arquitectura hexagonal innovadora.
Utiliza microservicios REST como agentes que interactúan con el core del sistema, compuesto por microservicios gRPC.
Esta estructura facilita la escalabilidad, mantenibilidad y la independencia entre los componentes del sistema.

## Arquitectura del Proyecto
### Componentes
- **Agentes REST**: Microservicios que reciben y manejan peticiones REST.
- **Core con Microservicios gRPC**: Lógica de negocio implementada mediante microservicios que comunican usando gRPC.
### Flujo de Datos
- Las peticiones REST son recibidas por los agentes.
- Los agentes transforman estas peticiones en llamadas gRPC.
- El core procesa estas llamadas y retorna la respuesta.

## Tecnologías Utilizadas
- **Spring Boot**: Para el desarrollo de microservicios REST.
- **gRPC**: Para la comunicación eficiente entre microservicios.
- **Docker**: Para la contenerización y fácil despliegue de los servicios.
- **Kubernetes**: Para la orquestación y manejo de los contenedores.

## Cómo Empezar
### Requisitos Previos
- Docker y Kubernetes instalados.
- Conocimientos básicos en Spring Boot y gRPC.
### Instalación y Despliegue
1. Clonar el repositorio: `git clone [url-del-repositorio]`.
2. Construir los contenedores Docker: `docker-compose up`.
3. Desplegar en Kubernetes: `kubectl apply -f k8s-config.yml`.

## Contribuciones
### Directrices para Contribuir
- Fork del repositorio.
- Crear una nueva rama para la característica o corrección.
- Realizar los cambios.
- Enviar un Pull Request con una descripción detallada.
### Estándares de Código
- Seguir las convenciones de codificación de Java y gRPC.
- Escribir tests para nuevas características.
- Documentar los cambios significativos.

## Documentación Adicional
- Arquitectura Hexagonal: Una descripción detallada de la arquitectura hexagonal.
- gRPC vs REST: Comparativa y razones para elegir gRPC en el core.

## Equipo de Desarrollo
- [Nombre del Desarrollador](enlace-github)
- [Otro Colaborador](enlace-colaborador)

## Licencia
Este proyecto está bajo la Licencia MIT - ver el archivo `LICENSE.md` para detalles.

## Contacto
- [Correo Electrónico](mailto:contacto@hexamicro.com)
- [GitHub Issues](enlace-a-issues-del-proyecto) para reportar bugs o solicitar características.

