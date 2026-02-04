package no.nav.aareg.dist.api.status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static no.nav.aareg.dist.api.status.Status.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/status", produces = { APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE })
@Slf4j
public class StatusController {

    @GetMapping
    public StatusResponse getStatus() {
        return new StatusResponse(OK, "");
    }
}
