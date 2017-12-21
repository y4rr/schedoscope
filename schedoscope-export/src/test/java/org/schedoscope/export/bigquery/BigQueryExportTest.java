package org.schedoscope.export.bigquery;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.storage.Storage;
import org.apache.thrift.TException;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.schedoscope.export.HiveUnitBaseTest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.schedoscope.export.utils.BigQueryUtils.*;
import static org.schedoscope.export.utils.CloudStorageUtils.*;

public class BigQueryExportTest extends HiveUnitBaseTest {

    final private static boolean CALL_BIG_QUERY = false;

    final private static boolean CLEAN_UP_BIG_QUERY = true;

    private BigQuery bigQuery;

    private Storage storage;

    @Override
    public void setUp() throws Exception {
        if (!CALL_BIG_QUERY)
            return;

        super.setUp();

        bigQuery = bigQueryService();
        storage = storageService();

        if (existsDataset(bigQuery, null, "default"))
            dropDataset(bigQuery, null, "default");

        createBucket(storage, "schedoscope_export_big_query_full_test", "europe-west3");

        setUpHiveServer("src/test/resources/test_map_data.txt",
                "src/test/resources/test_map.hql", "test_map");

    }

    @Test
    public void runBigQueryExportJob() throws CmdLineException, IOException, InterruptedException, TException, TimeoutException, ClassNotFoundException {

        if (!CALL_BIG_QUERY)
            return;

        BigQueryExportJob job = new BigQueryExportJob(conf);

        job.run(new String[]{
                "-m", "",
                "-d", "default",
                "-t", "test_map",
                "-b", "schedoscope_export_big_query_full_test",
                "-D", "20150801"
        });


    }

    @Override
    public void tearDown() throws Exception {
        if (!CALL_BIG_QUERY)
            return;

        super.tearDown();

        if (!CLEAN_UP_BIG_QUERY)
            return;

        if (existsDataset(bigQuery, null, "default"))
            dropDataset(bigQuery, null, "default");

        deleteBucket(storage, "schedoscope_export_big_query_full_test");
    }
}
