package fr.lernejo.sqlinj.inspect;

import java.util.List;

public record SqlResult(List<String> headers, List<List<String>> rows) {
}
