package cn.izualzhy.config;

import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FeignConfig {
    // 注入Spring MVC消息转换器工厂
      @Autowired
      private ObjectFactory<HttpMessageConverters> messageConverters = null;

      /**
       * 此处需要注意， Bean的名称要和默认装配的保持一致
       * @return 编码器
       */
      @Bean(name = "feignDecoder")
      // 设置为"prototype"，代表只对当前客户端使用
      public Decoder clientDecoder() {
         return new SpringDecoder(messageConverters);
      }
}
