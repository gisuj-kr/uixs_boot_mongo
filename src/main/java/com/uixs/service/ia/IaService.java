package com.uixs.service.ia;

import java.util.List;
import java.util.Map;

import com.uixs.model.ia.dto.IaDTO;

public interface IaService {
	public List<IaDTO> selectIa();
	
	public List<IaDTO> getIa_tree(String mode, String site_code, String parent, List<IaDTO> result);
	
	public IaDTO insertIa(IaDTO iaDto);
	
	public int selectMaxSort(IaDTO iaDto);
	
	public IaDTO selectIaOne(String id);
	
	public int updateIa(IaDTO iaDto);
	
	public int deleteIa(String id);
	
	public int updateIaSort(String id, int sort);
	
	public int updateIaState(IaDTO dto);
}
