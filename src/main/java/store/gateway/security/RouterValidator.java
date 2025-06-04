package store.gateway.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

    private List<String> openApiEndpoints = List.of(
        "POST /auth/register",
        "POST /auth/login"
    );

    public Predicate<ServerHttpRequest> isSecured =
        request -> openApiEndpoints
            .stream()
            .noneMatch(uri -> {
                String[] parts = uri.split(" ");
                if (parts.length != 2) return false;
                
                final String method = parts[0];
                final String path = parts[1];
                final boolean deep = path.endsWith("/**");
                
                boolean methodMatch = "ANY".equalsIgnoreCase(method) || 
                    request.getMethod().toString().equalsIgnoreCase(method);
                    
                boolean pathMatch = request.getURI().getPath().equals(path) || 
                    (deep && request.getURI().getPath().startsWith(path.replace("/**", "")));
                    
                return methodMatch && pathMatch;
            });
}