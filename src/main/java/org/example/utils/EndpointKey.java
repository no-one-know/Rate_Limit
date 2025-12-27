package org.example.utils;

import org.springframework.http.HttpMethod;
import java.util.Objects;

public final class EndpointKey {

    private final HttpMethod method;
    private final String path;

    public EndpointKey(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndpointKey)) return false;
        EndpointKey that = (EndpointKey) o;
        return method == that.method && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
