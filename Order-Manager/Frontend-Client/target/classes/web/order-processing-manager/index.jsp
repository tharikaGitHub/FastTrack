<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.order.manager.ui.Client" %>
<%@ page import="org.wso2.carbon.order.manager.model.xsd.Item" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%
        String serverURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
        ConfigurationContext configContext =
                (ConfigurationContext) config.getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
        String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);

        Client client;
        Item[] orders;

        try {
            client = new Client(configContext, serverURL, cookie);
            orders = client.getItems();
        } catch (Exception e) {
            CarbonUIMessage.sendCarbonUIMessage(e.getMessage(), CarbonUIMessage.ERROR, request, e);
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try{
                String itemCode = request.getParameter("itemCode");
                String itemName = request.getParameter("itemName");
                Double itemPrice = Double.parseDouble(request.getParameter("itemPrice"));
        		Item newItem = new Item();
        		newItem.setId(itemCode);
        		newItem.setName(itemName);
                newItem.setPrice(itemPrice);

                client.addItem(newItem);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
%>

<div id="middle">
    <h2>Order Management</h2>

    <div id="workArea">
        <table class="styledLeft" id="moduleTable">
                <thead>
                    <tr>
                        <th width="20%">Item Code</th>
                        <th width="40%">Item Name</th>
                        <th width="40%">Price($)</th>
                    </tr>
                </thead>
                <tbody>
           <%
                for(Item order:orders){
           %>
                   <tr>
                       <td><%=order.getId()%></td>
                       <td><%=order.getName()%></td>
                       <td><%=order.getPrice()%></td>
                    </tr>
           <%
                 }
           %>
                </tbody>
         </table>
         </br>
         </br>
         </br>
         <form action="" method="POST">
             <table>
                 <tr>
                     <td>Item Code</td>
                     <td><input type="text" id="itemCode" name="itemCode"></td>
                 </tr>
                 <tr>
                     <td>Item Name</td>
                     <td><input type="text" id="itemName" name="itemName"></td>
                 </tr>
                 <tr>
                     <td>Item Price</td>
                     <td><input type="text" id="itemPrice" name="itemPrice"></td>
                 </tr>
                 <tr>
                     <input type="submit" value="Add">
                 </tr>
             </table>
         </form>
    </div>
</div>