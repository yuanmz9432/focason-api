package com.lemonico.store.dao;



import com.lemonico.common.bean.Ms016_macro;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

;

@Mapper
public interface MacroSettingDao
{

    /**
     * @Param: message
     * @param: client_id
     * @description:マクロ一覧
     * @return: List<Ms016_macro>r
     * @author: hzm
     * @date: 2021/2/25
     */
    List<Ms016_macro> getMacroList(@Param("client_id") String client_id);

    /**
     * 获取マクロ详细
     *
     * @param id
     * @return
     */
    List<Ms016_macro> getMacroInfoById(@Param("id") String id);

    /**
     * @Description: //TODO Ms016新规保存
     *               @Date： 2021/3/29
     * @Param：
     * @return：Integer
     */
    Integer insertMacroData(Ms016_macro macro);

    /**
     * @Description: //TODO Ms016修改保存
     *               @Date： 2021/3/29
     * @Param：
     * @return：Integer
     */
    Integer updateMacroById(Ms016_macro macro);

    /**
     * @Description: //TODO 批量删除macro设定
     *               @Date： 2021/3/29
     * @Param：
     * @return：Integer
     */
    Integer delMacro(List<String> list, String client_id);

    /**
     * @Param:
     * @description: 检验macro优先顺序是否重复
     * @return: Integer
     * @date: 2021/04/06
     */
    Integer checkMacroPriorityExists(@Param("priority") Integer priority,
        @Param("client_id") String client_id);

    /**
     * @Description: //TODO macro信息修改
     *               @Date： 2021/4/6
     *               @Param：
     *               @return： Integer
     */
    Integer updateMacroInfo(@Param("client_id") String client_id,
        @Param("priority") Integer priority,
        @Param("macro_status") Integer macro_status,
        @Param("id") String id);

    /**
     * @Description: //TODO macro优先顺位重复信息修改
     *               @Date： 2021/8/3
     *               @Param：
     *               @return： Integer
     */
    Integer updateRepeatMacroInfo(@Param("client_id") String client_id,
        @Param("repeat_id") String repeat_id,
        @Param("old_priority") Integer old_priority);

    /**
     * @Description: //TODO 获取店铺最大优先级
     *               @Date： 2021/4/6
     *               @Param：
     *               @return： Integer
     */
    Integer getMaxPriority(@Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 检验macro名称是否重复
     * @return: boolean
     * @date: 2021/04/06
     */
    Integer nameCheck(@Param("macro_name") String macro_name,
        @Param("client_id") String client_id);
}
