package eu.h2020.symbiote.rh.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Configuration
//@EnableWebMvc
class MvcConf extends WebMvcConfigurationSupport {
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	MappingJackson2HttpMessageConverter a = converter();
        converters.add(a);
        addDefaultHttpMessageConverters(converters);
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
    	CloudResourceMessageConverter  converter = new CloudResourceMessageConverter ();
        //do your customizations here...
    	ObjectMapper objectMapper = new ObjectMapper();
    	List<MediaType> types = new ArrayList<>(1);
        types.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(types);
        
    	converter.setObjectMapper(objectMapper);
    	
        return converter;
    }
    
    public class CloudResourceMessageConverter extends MappingJackson2HttpMessageConverter {

		@Override
		protected boolean supports(Class<?> clazz) {
			System.out.println("hola");
			return false;
		}
    }

}
/*public class ApplicationSpringConfig extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters( List<HttpMessageConverter<?>> converters ) {
        converters.add(converter());
    }

    @Bean
    MappingJackson2HttpMessageConverter converter() {
        // [...]
    }
}

public class CloudResourceMessageConverter extends AbstractHttpMessageConverter<ArrayList<CloudResource>> {
	  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
	     if (mediaType==null) return true;
	     return  mediaType.equals(MediaType.APPLICATION_JSON);
	   }
	  protected void writeInternal(ArrayList<MessageContent> messageContentArrayList, HttpOutputMessage httpOutputMessage) throws       
	    IOException, HttpMessageNotWritableException {
	    StringBuffer sb = new StringBuffer();
	    sb.append("{\"myPersonalizedList\": {\n");
	    sb.append("    \"numitems\": \""+messageContentArrayList.size()+"\",\n    \"items\": [\n");
	    for(int i=0; i<messageContentArrayList.size();i++){
	      MessageContent messageContent = messageContentArrayList.get(i);
	      sb.append("       {\n          \"position\": \""+i+"\",\n");
	      sb.append("          \"data\": {\n");
	      sb.append("             \"name\": \""+messageContent.getName()+"\",\n             \"value\": \""+messageContent.getNumber()+"\"\n");
	      sb.append("          }\n");
	      if (i==(messageContentArrayList.size()-1))   sb.append("       }\n"); else  sb.append("       },\n");
	    }
	    sb.append("    ]\n  }\n}\n");
	    httpOutputMessage.getBody().write(sb.toString().getBytes());}
	@Override
	protected boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected ArrayList<CloudResource> readInternal(Class<? extends ArrayList<CloudResource>> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void writeInternal(ArrayList<CloudResource> t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		// TODO Auto-generated method stub
		
	}*/
