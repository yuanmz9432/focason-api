package com.lemonico.store.service.impl;



import com.lemonico.common.bean.Ms012_sponsor_master;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.SponsorDao;
import com.lemonico.store.service.SponsorSerivce;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: sunlogic
 * @description: 依頼主マスタ
 * @create: 2020-05-27 14:56
 **/
@Service
public class SponsorSerivceImpl implements SponsorSerivce
{

    @Autowired
    private SponsorDao sponsorDao;

    /**
     * @Description: 依頼主マスタ取得
     * @Param: 顧客CD, sponsor_default:true デフォルト
     * @return: Json
     * @Date: 2020/5/27
     */
    @Override
    public List<Ms012_sponsor_master> getSponsorList(String client_id, boolean sponsor_default, String sponsor_id) {
        List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(client_id, sponsor_default, sponsor_id);
        if (StringTools.isNullOrEmpty(sponsor_id)) {
            Iterator<Ms012_sponsor_master> it = sponsorList.iterator();
            while (it.hasNext()) {
                if ("99999".equals(it.next().getUtilize())) {
                    it.remove();
                }
            }
        }

        return sponsorList;
    }
}
