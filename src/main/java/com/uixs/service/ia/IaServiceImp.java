package com.uixs.service.ia;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uixs.model.ia.dao.IaDAO;
import com.uixs.model.ia.dto.IaDTO;

@Service
public class IaServiceImp implements IaService {
	
	@Autowired
	IaDAO iaDao;

	@Override
	public List<IaDTO> selectIa() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IaDTO> getIa_tree(String mode, String site_code, String parent, List<IaDTO> result) {
		// TODO Auto-generated method stub
		
		List<IaDTO> returnDto = iaDao.getIa_tree(mode, site_code, parent);
		
		if (mode == null && parent != null) {
        	// 각 자식에 대해 재귀적으로 자손 노드들을 찾음
        	if (returnDto != null) {
        		for (IaDTO child : returnDto) {
        			getIa_tree(mode, site_code, child.getId(), result);
        		}
        		result.addAll(returnDto);
        	}
        }
		else {
			result.addAll(returnDto);
		}
		
		return result;
	}

	@Override
	public IaDTO insertIa(IaDTO iaDto) {
		// TODO Auto-generated method stub
		return iaDao.insertIa(iaDto);
	}

	@Override
	public int selectMaxSort(IaDTO iaDto) {
		// TODO Auto-generated method stub
		return iaDao.selectMaxSort(iaDto);
	}

	@Override
	public IaDTO selectIaOne(String id) {
		// TODO Auto-generated method stub
		return iaDao.selectIaOne(id);
	}

	@Override
	public int updateIa(IaDTO iaDto) {
		// TODO Auto-generated method stub
		return iaDao.updateIa(iaDto);
	}

	@Override
	public int deleteIa(String id) {
		// TODO Auto-generated method stub
		return iaDao.deleteIa(id);
	}

	@Override
	public int updateIaSort(String id, int sort) {
		// TODO Auto-generated method stub
		return iaDao.updateIaSort(id, sort);
		
	}

	@Override
	public int updateIaState(IaDTO dto) {
		// TODO Auto-generated method stub
		return iaDao.updateIaState(dto);
	}

}
