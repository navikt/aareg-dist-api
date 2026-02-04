package no.nav.aareg.dist.api.graphql.common;

import static no.nav.aareg.dist.api.graphql.common.QueryDataElements.ROOT_ELEMENT_ARBEIDSFORHOLD;

public final class Queries {

    public static final String QUERY_ARBEIDSFORHOLD = "finnArbeidsforhold";

    private Queries() {
    }

    public static String getDataRootElement(String queryName) {
        if (queryName.equals(QUERY_ARBEIDSFORHOLD)) {
            return ROOT_ELEMENT_ARBEIDSFORHOLD;
        }
        return null;
    }
}
