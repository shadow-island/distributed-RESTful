package com.company.ucm.rest.handler.failover;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
// changed 
import com.company.ucc.model.shared.cluster.ClusterDTO;
import com.company.ucc.model.shared.cluster.ClusterID;
import com.company.ucc.model.shared.cluster.Clusters;
import com.company.ucc.msgs.shared.msg.cluster.ClusterMsgTypesToServer;
import com.company.ucm.dao.vertx.DAOFunctionType;
import com.company.ucm.dao.vertx.client.DAOFunctionClient;
import com.company.ucm.rest.RestRequest;
import com.company.ucm.rest.RestResponse;
import com.company.ucm.rest.handler.AbstractAuthRestHandler;
import com.company.ucm.rest.handler.RestHandlerManager;
import com.company.ucm.rest.msg.RestMessageBus;
import com.company.vertx.log.LogProvider;

@Singleton
public final class FailoverGroupNameHandler extends AbstractAuthRestHandler {
	
	private final RestMessageBus _msgSender;
	private final DAOFunctionClient _functionClient;

	@Inject
	public FailoverGroupNameHandler(
			final RestHandlerManager manager,
			final LogProvider logProvider,
			final DAOFunctionClient functionClient,
			final RestMessageBus msgSender,
			final EventBus googleEventBus,
			final ObjectMapper objectMapper) {
		super(manager, msgSender, googleEventBus, objectMapper);
		
		_msgSender = msgSender;
		_functionClient = functionClient;		
	}

	@Override
	public String getForPath() {		
		return "/failover-group/:name";
	}

	@Override
	public HttpMethod[] getAllowMethods() {
		return new HttpMethod[] {HttpMethod.POST};
	}

	@Override
	public void post(final RestRequest request, final Handler<RestResponse> responseHandler) {
				
		_functionClient.call(DAOFunctionType.FIND_ALL_CLUSTERS, 
			result -> {				
				final String name = request.getParamAsString("name");
				ClusterID clusterID = null;
				
				Clusters clustersObj = (Clusters) result;
				List<ClusterDTO> clusters = clustersObj.getClusters();
				for (ClusterDTO cluster : clusters) {
					if (cluster.getClusterName().equals(name)) {
						clusterID = cluster.getID();
						break;
					}	
				}			
				
				if (clusterID == null) {
					final ClusterDTO payload = new ClusterDTO(new ClusterID(), name);
					_msgSender.sendMsg(ClusterMsgTypesToServer.CREATE_CLUSTER, payload, _actionHandler);
				} else {
					_msgSender.sendMsg(ClusterMsgTypesToServer.ADD_TO_CLUSTER, clusterID, _actionHandler);
				}
			}
		);
	}		
}
