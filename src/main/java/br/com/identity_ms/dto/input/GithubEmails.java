package br.com.identity_ms.dto.input;

public record GithubEmails(

        String email,
        Boolean primary,
        Boolean verified,
        String visibility

) {
}
