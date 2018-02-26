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

    String RESOURCE_REGISTRATION_KEY_NAME = "symbIoTe.rap.registrationHandler.register_resources";
    String RESOURCE_UNREGISTRATION_KEY_NAME = "symbIoTe.rap.registrationHandler.unregister_resources";
    String RESOURCE_UPDATED_KEY_NAME = "symbIoTe.rap.registrationHandler.update_resources";

    String RESOURCE_LOCAL_UPDATE_KEY_NAME = "symbIoTe.rh.update_local_resources";
    String RESOURCE_LOCAL_UPDATED_KEY_NAME = "symbIoTe.rh.updated_local_resources";

    String RESOURCE_LOCAL_REMOVE_KEY_NAME = "symbIoTe.rh.remove_local_resources";
    String RESOURCE_LOCAL_REMOVED_KEY_NAME = "symbIoTe.rh.removed_local_resources";

    String RESOURCE_LOCAL_SHARE_KEY_NAME = "symbIoTe.rh.share_local_resources";
    String RESOURCE_LOCAL_UNSHARE_KEY_NAME = "symbIoTe.rh.unshare_local_resources";
}
