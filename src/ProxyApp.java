import java.util.ArrayList;
import java.util.List;

class Request {
    String userId;
    String payload;
    String uuid;

    public Request(Builder builder) {
        this.uuid = builder.uuid;
        this.userId = builder.userId;
        this.payload = builder.payload;
    }

    static class Builder {
        String userId;
        String payload;
        String uuid;

        Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        Request build() {
            return new Request(this);
        }
    }
}

class Response {

}

interface Handler {
    boolean handle(Request request);

    void setNext(Handler next);
}

abstract class AbstractHandler implements Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    protected boolean handleNext(Request request) {
        return next == null || next.handle(request);
    }
}


class AuthenticationHandler extends AbstractHandler {


    @Override
    public boolean handle(Request request) {
        if (request.userId == null) return false;
        return handleNext(request);
    }
}

class RateLimitHandler extends AbstractHandler {

    @Override
    public boolean handle(Request request) {
        if (request.userId == null) return false;
        return handleNext(request);
    }
}

class ValidationHandler extends AbstractHandler {

    @Override
    public boolean handle(Request request) {
        if (request.userId == null) return false;
        return handleNext(request);
    }
}

interface ApiHandler {
    Response handle(Request request);
}

class ApiService implements ApiHandler {

    @Override
    public Response handle(Request request) {
        return new Response();
    }
}





interface ApiRequest {
    boolean accept(Handler chain);
}

class SingleRequest implements ApiRequest {
    private final Request request;

    SingleRequest(Request request) {
        this.request = request;
    }

    @Override
    public boolean accept(Handler chain) {
        return chain.handle(request);
    }

    public Request getRequest() {
        return request;
    }
}


class BatchRequest implements ApiRequest {
    private final List<SingleRequest> requests = new ArrayList<>();

    void add(SingleRequest request) {
        requests.add(request);
    }

    @Override
    public boolean accept(Handler chain) {
        for (SingleRequest req : requests) {
            if (!req.accept(chain)) return false;
        }
        return true;
    }

    public List<SingleRequest> getRequests() {
        return requests;
    }
}

class ApiGatewayProxy {

    private final ApiHandler apiService;
    private final Handler chain;

    ApiGatewayProxy(ApiHandler apiService, Handler chain) {
        this.apiService = apiService;
        this.chain = chain;
    }

    public List<Response> handle(ApiRequest request) {

        if (!request.accept(chain)) {
            throw new RuntimeException("Request rejected");
        }

        List<Response> responses = new ArrayList<>();

        if (request instanceof BatchRequest batch) {
            for (SingleRequest r : batch.getRequests()) {
                responses.add(apiService.handle(r.getRequest()));
            }
        } else if (request instanceof SingleRequest single) {
            responses.add(apiService.handle(single.getRequest()));
        }

        return responses;
    }
}


public class ProxyApp {
}
