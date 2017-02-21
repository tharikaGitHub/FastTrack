/*
 *
 *   Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package mediator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import java.util.Iterator;

public class CustomClassMediator extends AbstractMediator {

    private static final Log logger = LogFactory.getLog(CustomClassMediator.class);

    /**
     * This is the method used for intercepting the message context and performing required operations.
     *
     * @param messageContext - message context
     * @return - true after successful mediation
     */
    public boolean mediate(MessageContext messageContext) {

        logger.info("Inside the mediate method");

        Iterator itr = messageContext.getEnvelope().getBody().getFirstElement().getChildElements();

        while (itr.hasNext()) {
            logger.info(itr.next());
        }

        logger.info("Printed the return message from server");
        return true;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setTraceState(int traceState) {
        traceState = 0;
    }

    @Override
    public int getTraceState() {
        return 0;
    }

}