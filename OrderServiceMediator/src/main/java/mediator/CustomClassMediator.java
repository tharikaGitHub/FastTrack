package mediator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.Iterator;

/**
 * Created by tharika on 2/20/17.
 */

public class CustomClassMediator extends AbstractMediator {

    private static final Log logger = LogFactory.getLog(CustomClassMediator.class);

    public boolean mediate(MessageContext messageContext) {

        logger.info("Inside the mediate method");

        Iterator itr = messageContext.getEnvelope().getBody().getFirstElement().getChildElements();

        while(itr.hasNext()) {
            logger.info(itr.next());
        }

        logger.info("Printed the return message from server");
        return true;
    }

    public String getType() {
        return null;
    }

    public void setTraceState(int traceState) {
        traceState = 0;
    }

    public int getTraceState() {
        return 0;
    }

}