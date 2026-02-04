package no.nav.aareg.dist.api.metrics;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_KONSUMENT_DATABEHANDLER_ORGNUMMER;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_KONSUMENT_OPERATION;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_KONSUMENT_ORGNUMMER;
import static no.nav.aareg.dist.api.request.RequestAttributes.ATTRIBUTE_AAREG_SERVER_NAVN;
import static no.nav.aareg.dist.api.request.RequestAttributes.TAG_CONSUMER;
import static no.nav.aareg.dist.api.request.RequestAttributes.TAG_DATABEHANDLER;
import static no.nav.aareg.dist.api.request.RequestAttributes.TAG_OPERATION;
import static no.nav.aareg.dist.api.request.RequestAttributes.TAG_SERVER;

@Component
public class ExtendedServerRequestObservationConvention extends DefaultServerRequestObservationConvention {

    private static final String TAG_VALUE_UKJENT = "ukjent";
    private static final String TAG_NONE = "none";

    @Override
    public KeyValues getLowCardinalityKeyValues(ServerRequestObservationContext context) {
        return super.getLowCardinalityKeyValues(context).and(extraKeyValues(context));
    }

    protected List<KeyValue> extraKeyValues(ServerRequestObservationContext context) {
        var konsumentSystem = context.getCarrier().getAttribute(ATTRIBUTE_AAREG_KONSUMENT_ORGNUMMER);
        var serverName = context.getCarrier().getAttribute(ATTRIBUTE_AAREG_SERVER_NAVN);
        var databehandler = context.getCarrier().getAttribute(ATTRIBUTE_AAREG_KONSUMENT_DATABEHANDLER_ORGNUMMER);
        var operation = context.getCarrier().getAttribute(ATTRIBUTE_AAREG_KONSUMENT_OPERATION);

        return List.of(
                KeyValue.of(TAG_CONSUMER, konsumentSystem != null ? konsumentSystem.toString() : TAG_VALUE_UKJENT),
                KeyValue.of(TAG_SERVER, serverName != null ? serverName.toString() : TAG_VALUE_UKJENT),
                KeyValue.of(TAG_DATABEHANDLER, databehandler != null ? databehandler.toString() : TAG_NONE),
                KeyValue.of(TAG_OPERATION, operation != null ? operation.toString() : TAG_VALUE_UKJENT)
        );
    }

}
