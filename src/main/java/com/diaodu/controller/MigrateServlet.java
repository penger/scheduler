package com.diaodu.controller;

import com.diaodu.domain.Constants;
import com.diaodu.ssh.SshUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by GP39 on 2017/1/7.
 */
public class MigrateServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String commandPath="/home/bigdatadev/gongpeng/tongbu.sh";
        String db = req.getParameter("db");
        if (StringUtils.isEmpty(db)){
            req.getRequestDispatcher("/WEB-INF/view/util/migrate.jsp").forward(req, resp);
        }else{
            String table = req.getParameter("table");
            String platform = req.getParameter("platform");
            Map<String, String> map = SshUtil.exeLocal("sh " + commandPath + " " + db + " " + table + " "+platform);
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print("<font color='red'>如果多分区:只会导入最后一个分区的数据</font>");
            out.print(map.get(Constants.RUNNING_MESSAGE));
        }
    }
}
