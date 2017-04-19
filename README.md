# cas 4.x

# 介绍
创建者： Andy (andy_chen314@163.com);

# 使用
1、redis配置
    修改 /cas-server-webapp/src/main/webapp/WEB-INF/cas.properties 文件内的参数：

    redis.database.num=0
    redis.database.hosts=127.0.0.1
    redis.database.port=6389
    st.timeout=20
    tgt.timeout=3600
    说明：   redis.database.num      redis 第几个库
            redis.database.hosts    redis服务器地址
            redis.database.port     redis服务端口
            st.timeout              ST的有效时间 单位--秒
            tgt.timeout             tgt的有效时间 单位--秒
       
2、用户验证器

    增加 AuthenticationHandler 实现类，将AuthenticationHandler 实现类配置在
    /cas-server-webapp/src/main/webapp/WEB-INF/deployerConfigContext.xml 文件中，
    增加到 PolicyBasedAuthenticationManager管理器中。
