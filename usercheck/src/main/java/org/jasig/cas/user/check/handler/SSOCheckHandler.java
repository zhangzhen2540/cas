package org.jasig.cas.user.check.handler;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.*;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by csf on 2014/7/22.
 *
 * @author csf
 */
public class SSOCheckHandler implements AuthenticationHandler {

    private static Logger logger = Logger.getLogger(SSOCheckHandler.class);

    @Override
    public HandlerResult authenticate(Credential credential) throws GeneralSecurityException, PreventedException {
        assert credential != null;
        UsernamePasswordCredential userCheckCredential = (UsernamePasswordCredential) credential;
        Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
        //todo 调用数据库，查询用户信息是否匹配
        if (StringUtil.isNotEmpty(userCheckCredential.getUsername())) {
            logger.debug("登录用户：" + userCheckCredential.getUsername());
            if (StringUtil.stringEquals(userCheckCredential.getUsername(),userCheckCredential.getPassword())) {
                attributes.put(Constants.USER_INFO, JSON.toJSONString(userCheckCredential));
            } else {
                logger.error("用户名或密码错误");
                throw new FailedLoginException("用户名或密码错误");
            }
        } else {
            throw new FailedLoginException("用户名不能为空");
        }
        if (StringUtil.isEmpty((String) attributes.get(Constants.USER_INFO))) {
            throw new FailedLoginException();
        }
        return createHandlerResult(credential, new SimplePrincipal((String) attributes.get(Constants.USER_INFO), attributes));
    }

    @Override
    public boolean supports(Credential credential) {
        return credential instanceof UsernamePasswordCredential;
    }

    @Override
    public String getName() {
        return null;
    }

    protected final HandlerResult createHandlerResult(final Credential credential, final Principal principal) {
        return new HandlerResult(this, new BasicCredentialMetaData(credential), principal, null);
    }
}