//package org.k8loud.executor.actions.openstack;
//
//import data.Params;
//import org.k8loud.executor.exception.ActionException;
//import org.k8loud.executor.exception.OpenstackException;
//import org.k8loud.executor.openstack.OpenstackService;
//
//public class HorizontalScalingAction extends OpenstackAction {
//    private String region;
//    private String serverId;
//
//    public HorizontalScalingAction(Params params, OpenstackService openstackService) throws ActionException {
//        super(params, openstackService);
//    }
//
//    @Override
//    public void unpackParams(Params params) {
//        region = params.getRequiredParam("region");
//        serverId = params.getRequiredParam("serverId");
//    }
//
//    @Override
//    protected String executeBody() throws OpenstackException {
////        return openstackService.copyServer(region, serverId);
//        return openstackService.;
//        //FIXME this only create a server with same flavor and image
//        //TODO HorizontalScalingDown add (delete server and extra actions to be done before)
//    }
//}
