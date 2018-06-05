package eu.h2020.symbiote.rh.constants;

public interface RHConstants {

    String PLATFORM_ID = "platformId";
    String DO_CREATE_RESOURCES = "/platforms/{platformId}/resources";
    String DO_CREATE_RDF_RESOURCES = "/platforms/{platformId}/rdfResources";
    String DO_UPDATE_RESOURCES = "/platforms/{platformId}/resources";
    String DO_REMOVE_RESOURCES = "/platforms/{platformId}/resources";

    String DO_CLEAR_DATA = "/platforms/{platformId}/clearData";

    String RESOURCE_COLLECTION = "cloudResource";
    String DO_SHARE_RESOURCES = "/sharing";

    String RH_RESOURCE_TRUST_UPDATE_QUEUE_NAME = "symbIoTe.trust.rh.resource.update";
}
