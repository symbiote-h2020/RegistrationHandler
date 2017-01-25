package eu.h2020.symbiote.beans;

import java.util.ArrayList;
import java.util.List;
/**

 * Class containin a list of resources, @see eu.h2020.symbiote.beans.ResourceBean
 * @author: Elena Garrido
 * @version: 25/01/2017

 */
public class ResourceListBean {
    private List<ResourceBean> resources = new ArrayList<ResourceBean>();

	public List<ResourceBean> getResources() {
		return resources;
	}

	public void setResources(List<ResourceBean> resources) {
		this.resources = resources;
	}
}
