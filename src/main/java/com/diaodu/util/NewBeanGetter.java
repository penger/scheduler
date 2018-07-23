package com.diaodu.util;

import com.diaodu.db.JDBCUtils;
import com.diaodu.domain.Relation;
import com.diaodu.domain.Source;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by GP39 on 2016/9/26.
 */
public class NewBeanGetter {


    public static void main(String[] args ) throws Exception{
        Connection connection = JDBCUtils.getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Object[] param =new Object[]{"311f30b1cd384bccb36e784951437237"};
        List<Relation> list = queryRunner.query(connection,
                "select *,e.hive_table front_name from relation r left join etl e  on r.etl_id=e.etl_id  where front_id=? and type =3  ",
                new BeanListHandler<>(Relation.class),param);
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i).getFront_name());
            System.out.println(list.get(i).getFront_id());
        }
    }



}
