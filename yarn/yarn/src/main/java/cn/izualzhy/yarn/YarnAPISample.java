package cn.izualzhy.yarn;

import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class YarnAPISample {

    private YarnClient createYarnClient(String yarnConfDir) throws MalformedURLException {
        Path yarnSiteXml = Paths.get(yarnConfDir, "yarn-site.xml");
        Path mrSiteXml = Paths.get(yarnConfDir, "mapred-site.xml");

        YarnConfiguration yarnConfig = new YarnConfiguration();
        yarnConfig.addResource(yarnSiteXml.toUri().toURL());
        yarnConfig.addResource(mrSiteXml.toUri().toURL());

        YarnClient yarnclient = YarnClient.createYarnClient();
        yarnclient.init(yarnConfig);
        yarnclient.start();

        return yarnclient;
    }

    private void sample(String yarnConfDir) {
        try (YarnClient yarnClient = createYarnClient(yarnConfDir)) {
            System.out.println("yarnClient : " + yarnClient);
            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            for (NodeReport nodeReport : nodeReports) {
                System.out.printf("node: %s, %s%n", nodeReport.getNodeId(), nodeReport.getHttpAddress());
                System.out.println("\tcapacity:" + nodeReport.getCapability());
                System.out.println("\t\tvirtualCores:" + nodeReport.getCapability().getVirtualCores());
            }

        } catch (Exception e) {
            System.out.println("exception : " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.printf("yarn conf dir:%s%n", args[0]);

        new YarnAPISample().sample(args[0]);
    }
}
