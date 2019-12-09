import com.alibaba.fastjson.JSONObject;
import com.example.wx_pay.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.UUID;

@Controller
public class InterceptUtensil implements HandlerInterceptor {
  //设置不拦截
    final String[] URIS = {"/users/login","/pay/callback","/pay/refund/callBack"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject res = new JSONObject();
        res.put("data", null);
        res.put("page", null);
        PrintWriter pw = null;

        String token = request.getHeader("token");

        System.out.println("uri："+request.getServletPath());

        for(String uri:URIS) {
            if(request.getServletPath().contains(uri)) {
                System.out.println("过了");
                return true;
            }
        }

        if(token == null){
            pw = response.getWriter();
            res.put("code", 0);

            pw.write(res.toString());
            return false;
        }

        String key= UserUtils.users.get(token);
        if(key == null) {
            pw = response.getWriter();
            res.put("code", 10000);
            pw.write(res.toString());
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
