package net.ripe.db.whois.api.changedphase3;

import net.ripe.db.whois.api.RestTest;
import net.ripe.db.whois.api.rest.domain.WhoisResources;
import net.ripe.db.whois.api.rest.mapper.FormattedClientAttributeMapper;
import net.ripe.db.whois.common.rpsl.AttributeType;
import net.ripe.db.whois.common.rpsl.RpslAttribute;
import net.ripe.db.whois.common.rpsl.RpslObject;
import net.ripe.db.whois.common.rpsl.RpslObjectBuilder;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public abstract class AbstactScenarioRunner implements ScenarioRunner {

    protected static final RpslObject TEST_OBJECT = RpslObject.parse("" +
            "mntner:        TESTING-MNT\n" +
            "descr:         Test maintainer\n" +
            "admin-c:       TP1-TEST\n" +
            "upd-to:        upd-to@ripe.net\n" +
            "mnt-nfy:       mnt-nfy@ripe.net\n" +
            "auth:          MD5-PW $1$EmukTVYX$Z6fWZT8EAzHoOJTQI6jFJ1  # 123\n" +
            "mnt-by:        OWNER-MNT\n" +
            "mnt-by:        TESTING-MNT\n" +
            "source:        TEST");
    protected static String CHANGED_VALUE = "test@ripe.net 20121016";
    protected Context context;

    public AbstactScenarioRunner(Context context) {
        this.context = context;
    }

    public void before(Scenario scenario) {
        // delete if exists
        if (doesObjectExist()) {
            doDelete("TESTING-MNT");
        }

        // create if needed
        if (scenario.getPreCond() == Scenario.ObjectStatus.OBJ_EXISTS_WITH_CHANGED) {
            doCreate(MNTNER_WITH_CHANGED());
        } else if (scenario.getPreCond() == Scenario.ObjectStatus.OBJ_EXISTS_NO_CHANGED__) {
            doCreate(MNTNER_WITHOUT_CHANGED());
        }
    }

    public void after(Scenario scenario) {
    }

    protected RpslObject MNTNER_WITHOUT_CHANGED() {
        return TEST_OBJECT;
    }

    protected RpslObject MNTNER_WITH_CHANGED() {
        return new RpslObjectBuilder(TEST_OBJECT)
                .addAttributeSorted(new RpslAttribute(AttributeType.CHANGED, CHANGED_VALUE))
                .get();
    }

    protected boolean doesObjectExist() {
        boolean exists = false;
        try {
            RestTest.target(context.getRestPort(), "whois/test/mntner/TESTING-MNT")
                    .request()
                    .get(WhoisResources.class);
            exists = true;
        } catch (NotFoundException nf) {
            // swallow
        }
        return exists;
    }


    protected void doDelete(final String uid) {
        try {

            RestTest.target(context.getRestPort(), "whois/test/mntner/" + uid + "?password=123")
                    .request()
                    .delete(WhoisResources.class);
        } catch (ClientErrorException exc) {
            throw exc;
        }
    }

    protected void doCreate(final RpslObject obj) {
        try {
            RestTest.target(context.getRestPort(), "whois/test/mntner?password=123")
                    .request()
                    .post(Entity.entity(context.getWhoisObjectMapper().mapRpslObjects(FormattedClientAttributeMapper.class, obj), MediaType.APPLICATION_XML), WhoisResources.class);
        } catch (ClientErrorException exc) {
            WhoisResources whoisResources = exc.getResponse().readEntity(WhoisResources.class);
            throw exc;
        }
    }


//    protected void verifyResponse(final RpslObject obj, final boolean mustContainChanged) {
//        if (mustContainChanged) {
//            assertThat(obj.findAttribute(AttributeType.CHANGED).getValue(), is(CHANGED_VALUE));
//        } else {
//            assertThat(obj.containsAttribute(AttributeType.CHANGED), is(false));
//        }
//    }

//    protected void verifyMail(final boolean mustContainChanged) throws MessagingException, IOException {
//        final String message = mailSenderStub.getMessage("mnt-nfy@ripe.net").getContent().toString();
//
//        if (mustContainChanged) {
//            assertThat(message, containsString(CHANGED_VALUE));
//        } else {
//            assertThat(message, not(containsString(CHANGED_VALUE)));
//        }
//        assertFalse(mailSenderStub.anyMoreMessages());
//
//    }

    public void create(Scenario scenario) {
        throw new UnsupportedOperationException("Create method not supported for protocol " + getProtocolName());
    }

    public void modify(Scenario scenario) {
        throw new UnsupportedOperationException("Modify method not supported for protocol " + getProtocolName());
    }

    public void delete(Scenario scenario) {
        throw new UnsupportedOperationException("Delete method not supported for protocol " + getProtocolName());
    }

    public void get(Scenario scenario) {
        throw new UnsupportedOperationException("Get method not supported for protocol " + getProtocolName());
    }

    public void search(Scenario scenario) {
        throw new UnsupportedOperationException("Search method not supported for protocol " + getProtocolName());
    }

    public void event(Scenario scenario) {
        throw new UnsupportedOperationException("Event method not supported for protocol " + getProtocolName());
    }

    public void meta(Scenario scenario) {
        throw new UnsupportedOperationException("Meta method not supported for protocol " + getProtocolName());
    }

}
