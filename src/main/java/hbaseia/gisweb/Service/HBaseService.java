package hbaseia.gisweb.Service;

import model.QueryMatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.json.JSONObject;
import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;
import service.KNNQuery;
import service.WithinQuery;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;

public class HBaseService {
    private Logger log = LoggerFactory.getLogger(HBaseService.class);
    private Configuration conf = null;
    private Connection connection = null;
    private GeometryFactory factory = new GeometryFactory();
    private GeoJSONReader reader = new GeoJSONReader();
    private GeoJSONWriter writer = new GeoJSONWriter();


    public HBaseService(Configuration configuration) {
        this.conf = configuration;
        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            log.error("获取HBase连接失败");
        }
    }

    public GeoJSON query(String geojson) throws IOException {
        JSONObject obj = new JSONObject(geojson);
        String geo = obj.getJSONObject("geometry").toString();
        Geometry geometry = reader.read(geo);
        GeoJSON ret = null;
        if (geometry.getGeometryType().equals("Point")) {
            Coordinate coordinate = geometry.getCoordinate();
            KNNQuery query = new KNNQuery(connection);
            double lon = coordinate.x, lat = coordinate.y;
            Queue<QueryMatch> matches = query.queryKNN(lon, lat, 10);
            ret = formMultiPointJSONFromCollection(matches);
        }
        if (geometry.getGeometryType().equals("Polygon")) {
            WithinQuery query = new WithinQuery(connection);
            Set<QueryMatch> matches = query.queryWithFilter(geometry);
            ret = formMultiPointJSONFromCollection(matches);
        }
        return ret;
    }

    private GeoJSON formMultiPointJSONFromCollection(Collection<QueryMatch> queryMatches) {
        Coordinate[] coordinates = new Coordinate[queryMatches.size()];
        int i = 0;
        for (QueryMatch q: queryMatches) {
            coordinates[i++] = new Coordinate(q.lon, q.lat);
        }
        MultiPoint multiPoint = factory.createMultiPointFromCoords(coordinates);
        return writer.write(multiPoint);
    }

}
