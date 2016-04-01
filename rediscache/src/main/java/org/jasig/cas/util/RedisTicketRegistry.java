package org.jasig.cas.util;

import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.AbstractDistributedTicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class RedisTicketRegistry extends AbstractDistributedTicketRegistry {

    private int redisDatabaseNum;
    private int stTime;  //ST有效时间
    private int tgtTime; //TGT有效时间

    private JedisPool cachePool;
    private static Logger logger = LoggerFactory.getLogger(RedisTicketRegistry.class);

    public RedisTicketRegistry(int redisDatabaseNum, String hosts, int port, int stTime, int tgtTime) {
        this.redisDatabaseNum = redisDatabaseNum;
        this.stTime = stTime;
        this.tgtTime = tgtTime;
        cachePool = new JedisPool(new JedisPoolConfig(), hosts, port);
    }

    public void addTicket(Ticket ticket) {

        Jedis jedis = cachePool.getResource();
        jedis.select(redisDatabaseNum);

        int seconds = 0;

        String key = ticket.getId();

        if (ticket instanceof TicketGrantingTicket) {
            seconds = tgtTime;
        } else {
            seconds = stTime;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(ticket);

        } catch (Exception e) {
            logger.error("adding ticket to redis error.");
        } finally {
            try {
                if (null != oos) oos.close();
            } catch (Exception e) {
                logger.error("oos closing error when adding ticket to redis.");
            }
        }
        jedis.set(key.getBytes(), bos.toByteArray());
        jedis.expire(key.getBytes(), seconds);

        cachePool.returnResource(jedis);

    }

    public Ticket getTicket(final String ticketId) {
        return getProxiedTicketInstance(getRawTicket(ticketId));
    }


    private Ticket getRawTicket(final String ticketId) {
        System.err.println("get");
        if (null == ticketId) return null;

        Jedis jedis = cachePool.getResource();
        jedis.select(redisDatabaseNum);

        Ticket ticket = null;
        byte[] tickeValue = jedis.get(ticketId.getBytes());
        ObjectInputStream ois = null;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(tickeValue);
            if (null != bais) {
                ois = new ObjectInputStream(bais);
            }
            ticket = (Ticket) ois.readObject();
        } catch (Exception e) {
            logger.error("getting ticket to redis error.");
        } finally {
            try {
                if (null != ois) ois.close();
            } catch (Exception e) {
                logger.error("ois closing error when getting ticket to redis.");
            }
        }

        cachePool.returnResource(jedis);

        return ticket;
    }


    public boolean deleteTicket(final String ticketId) {

        if (ticketId == null) {
            return false;
        }
        System.err.println("delete");

        Jedis jedis = cachePool.getResource();
        jedis.select(redisDatabaseNum);

        jedis.del(ticketId.getBytes());

        cachePool.returnResource(jedis);

        return true;
    }

    public Collection<Ticket> getTickets() {

        throw new UnsupportedOperationException("GetTickets not supported.");

    }

    protected boolean needsCallback() {
        return false;
    }

    protected void updateTicket(final Ticket ticket) {
        addTicket(ticket);
    }

}
