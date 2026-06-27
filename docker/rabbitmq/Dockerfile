# RabbitMQ + rabbitmq_delayed_message_exchange plugin.
#
# The delayed-message plugin is NOT bundled with the stock image, so we layer it in: download the
# .ez community plugin (its major version must match the broker's) into the plugins dir and enable
# it offline. This lets producers publish to an x-delayed-message exchange with an x-delay header
# so the broker holds each message until its delay elapses — true per-message scheduled delivery.
FROM rabbitmq:4.1-management

# Plugin release must line up with the RabbitMQ 4.1.x line above.
ARG PLUGIN_VERSION=4.1.0

# ADD can fetch a remote URL directly (no curl/wget needed in the base image).
ADD --chmod=0644 \
    https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v${PLUGIN_VERSION}/rabbitmq_delayed_message_exchange-${PLUGIN_VERSION}.ez \
    /opt/rabbitmq/plugins/

RUN rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange
