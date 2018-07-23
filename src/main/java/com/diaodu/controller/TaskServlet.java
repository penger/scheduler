package com.diaodu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.alibaba.fastjson.JSONObject;
import com.diaodu.domain.Batch;
import com.diaodu.domain.Constants;
import com.diaodu.domain.Task;
import com.diaodu.service.ETLService;
import com.diaodu.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 创建任务
 * 作者：姜志成
链接：https://www.zhihu.com/question/63091863/answer/207991274
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

实际比赛的进程，前半段基本是按照剧本进行，木村翔上来就牟足劲猛攻，邹市明靠脚步移动保持距离打防守反击，虽然局势上是木村更积极主动，但邹市明也有更多的有效命中和更高的命中率，在这种没有压倒优势方的局势下，基本会是邹市明拿下更多的回合数。比赛的第一个转折点是第二还是第三回合时候，木村的眉骨在一次相撞中开了花，我当时就说这是对邹市明非常有利的一个机会，因为尽管这次开花和击打无关，但是邹市明可以通过一直攻击伤口让它持续流血，眼角眉骨这个地方想彻底止血是很难的。而如果出现无法止血裁判认定不能继续比赛的情况，那么可能会提前终止比赛——而这时点数占优的邹市明就将直接获得比赛的胜利（但若此时还没打满4回合，那么比赛将做无效处理而不会根据点数判罚比比赛结果）。即使无法把这个伤口打成伤停，持续的流血对于木村的视线和拳架也会有影响，对于想把比赛平稳拖到结束的邹市明来说肯定是好消息。而事实上邹市明确实也是很有经验的拳手，一直在找机会攻击木村受伤的右眼角，每个局间休息木村基本都要重新包扎止血看起来很危险，但这个伤口也并没有爆发成不可止住必须伤停的情况，也算运气眷顾了木村吧，因为如果因为无法止血伤停提前结束比赛，那么获胜方将肯定是邹市明。
作者：姜志成
链接：https://www.zhihu.com/question/63091863/answer/207991274
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

其实对邹市明，大家一开始的认识肯定都是奥运冠军、为中国军团夺得金牌的英雄选手。所以他转职业后，我也一直是抱着支持的态度，客观上他也确实带动
了我对于中国拳击乃至整个小级别拳击比赛的关注，毕竟之前普通的吃瓜拳迷大多看的还是四个中重量级的比赛，
对于112磅这样不太重要的小级别没什么认识。从他职业生涯第一场比赛开始到刚结束的这场卫冕战，我都在以“中国人”的身份为那么一位在国际舞台上与
人竞技的选手加油助威，说实话，输赢并不是我们这样的观众所看重的，就像即使中国跳水队丢了金牌，我们也一样非常支持他们衷心希望每个队员有好的发挥。
但邹市明暴露出的在拳台下的种种，确实有时难以让人“粉”起来。让我下决心写那么一篇文章的最后一个助推点，是比赛结束后，邹市明和他的主
办团队，居然没有给胜者木村翔应有的展示、表现时间，先是现场DJ不知所云的说了一套民族英雄主义宣传，然后邹市明拿起话筒办了个座谈会，
一会儿感谢好兄弟张杰林俊杰来现场看比赛给自己加油，一会儿说我们要以大国胸怀去容纳他们日本人，不要嘘他、辱骂他，一会儿说比赛只有一
个冠军但没有输家、自己在推动中国拳击事业发展上做了多少牛逼贡献是“先行者”，一会儿又好像在谈退休计划说要怎么推广拳击、带动国人热情和关注，
尬得要命不说，我感觉也一点不职业、实际缺乏对木村和所有观众以及拳击运动的尊重。当然这场拳赛本身办得也比较中国特色，充满了无厘头的花边活动
…… 反正至少这场比赛办下来从拳赛本身的准备到整个活动策划，都让我感觉邹市明冒着很大风险自行解约另立门户搞出的团队不太靠谱。
 */

public class TaskServlet extends HttpServlet {

    private static final long serialVersionUID = -8194525654889919217L;


    Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        TaskService taskService = new TaskService();
        String op = req.getParameter("op");
        if ("create".equals(op) || "c".equals(op)) {
            Task task = getBeanFromReqeust(req);
            //如果传入的task有并不是默认的-1 id 那么更新操作变为 先删除后添加
            String message = "";
            if (task.getId() != -1) {
                taskService.deleteTask(task.getId());
                message = message + "删除id为:" + task.getId() + "的task!";
            }
            //修改etl状态为已经调度:
            String etl_id_str = req.getParameter("etl_id");
            if (etl_id_str == null) {
                log.info("未与etl关联的操作");
            } else {
                int etl_id = Integer.parseInt(etl_id_str);
                ETLService etlService = new ETLService();
                int count = etlService.updateSchedulingByID(etl_id, 1);
                if (count == 1) {
                    message = message + "修改了ETL的为已调度状态<br/>";
                } else {
                    message += "ETL状态修改失败!<br/>";
                }
            }
            int rows = taskService.addTask(task);
            if (rows == 1) {
                message = message + "新增了task!<br/>";
            } else {
                message += "新增失败!<br/>";
            }
            writer.print(message);
            writer.close();
        } else if ("update".equals(op) || "u".equals(op)) {
            Task task = getBeanFromReqeust(req);
            int rows = taskService.updateTask(task);
            writer.println(rows);
            writer.close();
        } else if ("delete".equals(op) || "d".equals(op)) {
            String id = req.getParameter("id");
            int row = taskService.deleteTask(Integer.parseInt(id));
            writer.println(row);
            writer.close();
        } else if ("exec".equals(op)) {
            int id = Integer.parseInt(req.getParameter("id").trim());
            String atpre = req.getParameter("atpre");
            Task task = taskService.getTaskByID(id);
            Map<String, String> resultMap = taskService.executeTask(task, atpre);
            JSONObject jsonObject = new JSONObject();
            String jsonString = jsonObject.toJSONString(resultMap);
            writer.println(jsonString);
            writer.close();
        } else {
            req.getRequestDispatcher("/WEB-INF/view/index").forward(req, resp);
        }


    }


    private Task getBeanFromReqeust(HttpServletRequest req) {
        Task t = new Task();
        if (!req.getParameter("id").equals("")) {
            t.setId(Integer.parseInt(req.getParameter("id")));
        }
        if (!req.getParameter("seq").equals("")) {
            t.setSeq(Integer.parseInt(req.getParameter("seq")));
        }
        t.setTname(ifNullBlank(req.getParameter("tname")));
        t.setTdesc(ifNullBlank(req.getParameter("tdesc")));
        t.setBatchid(ifNullBlank(req.getParameter("batchid")));
        t.setTasktype(ifNullBlank(req.getParameter("tasktype")));
        t.setCommandpath(ifNullBlank(req.getParameter("commandpath")));
        t.setCommand(ifNullBlank(req.getParameter("command")));
        t.setArgs(ifNullBlank(req.getParameter("args")));
        t.setActor(ifNullBlank(req.getParameter("actor")));
        return t;
    }

    private String ifNullBlank(Object object) {
        if (object == null) {
            return "-";
        } else {
            try {
                return URLDecoder.decode("" + object, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }

    }

}
