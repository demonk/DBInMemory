package cn.demonk.dbinmemory.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.demonk.memorystorage.table.Aggregation;
import cn.demonk.memorystorage.table.Table;

@Table.TableName(name = "table_name")
public class LogTable {

    @Table.Key(name = "package", primary = true)
    String packageName;

    @Table.Key(name = "cycle", primary = true)
    long period;

    @Table.Key(name = "open_count", aggregation = Aggregation.SUM)
    int openCount = 0;

    @Table.Key(name = "time_cost", aggregation = Aggregation.SUM)
    long timeCost = 0;

    public LogTable() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        period = Long.parseLong(sdf.format(new Date()));
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setOpenCount(int count) {
        this.openCount = count;
    }

    public void setTimeCost(long costTime) {
        this.timeCost = costTime;
    }
}
