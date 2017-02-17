

/**
 * OrderProcessingManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1-wso2v10  Built on : Sep 04, 2013 (02:02:54 UTC)
 */

    package org.wso2.carbon.order.manager.stub;

    /*
     *  OrderProcessingManager java interface
     */

    public interface OrderProcessingManager {
          
       /**
         * Auto generated method signature for Asynchronous Invocations
         * 
         */
        public void  addOrder(
         org.wso2.carbon.order.manager.model.xsd.Item item1

        ) throws java.rmi.RemoteException
        
        ;

        

        /**
          * Auto generated method signature
          * 
                    * @param getOrders2
                
         */

         
                     public org.wso2.carbon.order.manager.model.xsd.Item[] getOrders(

                        )
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getOrders2
            
          */
        public void startgetOrders(

            

            final org.wso2.carbon.order.manager.stub.OrderProcessingManagerCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    