package org.deb.bbclive.tagging.opencalais;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenCalaisTagger {
	 /**
     * Open Calais API token.
     */
    @Value("${calais.api.token}")
    private String apiToken;
}
