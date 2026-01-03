import java.util.Map;

public class ApiRequestApp {
    enum METHOD {
        GET, POST;

    }

    static final class ApiRequest {
        final String endpoint;
        final METHOD method;
        Map<String, String> headers;
        String body;
        final int timeout;
        final boolean retryEnabled;

        private ApiRequest(Builder builder) {
            this.endpoint = builder.endpoint;
            this.method = builder.method;
            this.body = builder.body;
            this.headers = builder.headers;
            this.timeout = builder.timeout;
            this.retryEnabled = builder.retryEnabled;
        }

        Builder toBuilder() {
            return new Builder().endpoint(endpoint).method(method).headers(headers).body(body).timeout(timeout).retryEnabled(retryEnabled);
        }


        static class Builder {
            String endpoint;
            METHOD method;
            Map<String, String> headers;
            String body;
            int timeout;
            boolean retryEnabled;

            Builder endpoint(String endpoint) {
                if (endpoint == null) throw new IllegalArgumentException("Endpoint must be provided");
                this.endpoint = endpoint;
                return this;
            }

            Builder method(METHOD method) {
                this.method = method;
                return this;
            }

            Builder headers(Map<String, String> headers) {
                this.headers = headers;
                return this;
            }

            Builder body(String body) {
                this.body = body;
                return this;
            }

            Builder timeout(int timeout) {
                this.timeout = timeout;
                return this;
            }

            Builder retryEnabled(boolean retryEnabled) {
                this.retryEnabled = retryEnabled;
                return this;
            }

            ApiRequest build() {
                if (endpoint == null) throw new IllegalStateException("Endpoint is required");
                if (method == null) throw new IllegalStateException("Method is required");
                return new ApiRequest(this);
            }
        }
    }
}
