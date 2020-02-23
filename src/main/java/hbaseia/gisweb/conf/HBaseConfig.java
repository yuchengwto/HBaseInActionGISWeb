package hbaseia.gisweb.conf;


import hbaseia.gisweb.Service.HBaseService;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class HBaseConfig {

    @Value("${HBase.nodes}")
    private String nodes;

    @Bean
    public HBaseService getHbaseService() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", nodes);
        return new HBaseService(conf);
    }
}
