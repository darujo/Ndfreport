package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.converter.ValCursConverter;
import ru.daru_jo.dto.ValCurs;
import ru.daru_jo.entity.CursVal;
import ru.daru_jo.integration.CbrServiceIntegration;
import ru.daru_jo.repository.CursValRepository;
import ru.daru_jo.specifications.Specifications;

import java.sql.Timestamp;

@Service
public class ValuteService {
    private CursValRepository cursValRepository;

    @Autowired
    public void setCursValRepository(CursValRepository cursValRepository) {
        this.cursValRepository = cursValRepository;
    }

    private CbrServiceIntegration serviceIntegration;

    @Autowired
    public void setServiceIntegration(CbrServiceIntegration serviceIntegration) {
        this.serviceIntegration = serviceIntegration;
    }

    public void save(CursVal cursVal){
       CursVal cursValSave =  getCursVal(cursVal.getCharCode(),cursVal.getTimestamp());
       if(cursValSave!= null){
           cursVal.setId(cursValSave.getId());
       }
       cursValRepository.save(cursVal);
    }
    private final CursVal cursValRub = new CursVal(null,"810","RUB",1L,"Российские рубли",1d,1d,null,null);
    public CursVal getCursVal(String charCode, Timestamp timestamp){
        if (charCode.equals("RUB")){
            return cursValRub;
        }

        Specification<CursVal> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"charCode",charCode);
        specification = Specifications.eq(specification,"timestamp",timestamp);
        return cursValRepository.findOne(specification).orElse(null);
    }

    public CursVal getCursValAnaUpdate(String charCode, Timestamp timestamp){
        CursVal cursVal = getCursVal(charCode,timestamp);
        if (cursVal != null){
            return cursVal;
        } else {
            loadValCurs(timestamp);
            return getCursVal(charCode,timestamp);

        }
    }

    public void loadValCurs(Timestamp timestamp){
        ValCurs valCurs = serviceIntegration.userVacationStart(timestamp);
        valCurs.getValuteList().forEach(valute -> save(ValCursConverter.getCursVal(timestamp, valute)));
    }

//    public void delete(CursVal cursVal){
//        cursValRepository.delete(cursVal);
//    }


}
