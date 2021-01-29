package com.xtra.api.projection.admin;

import lombok.Data;

@Data
public class MediaPair<A, B> {
    private final A id;
    private final B name;

    public MediaPair(A first, B second) {
        super();
        this.id = first;
        this.name = second;
    }

    public int hashCode() {
        int hashFirst = id != null ? id.hashCode() : 0;
        int hashSecond = name != null ? name.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof MediaPair) {
            MediaPair<?, ?> otherPair = (MediaPair<?, ?>) other;
            return
                    ((this.id == otherPair.id ||
                            (this.id != null && otherPair.id != null &&
                                    this.id.equals(otherPair.id))) &&
                            (this.name == otherPair.name ||
                                    (this.name != null && otherPair.name != null &&
                                            this.name.equals(otherPair.name))));
        }

        return false;
    }

    public String toString() {
        return "(" + id + ", " + name + ")";
    }
}
