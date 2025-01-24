package gongnon.domain.hotnews.service;

import gongnon.domain.hotnews.model.PredefinedPress;
import gongnon.domain.hotnews.repository.PredefinedPressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PredefinedPressService {
    @Autowired
    private PredefinedPressRepository predefinedPressRepository;

    public List<PredefinedPress> getAllPress() {
        return predefinedPressRepository.findAll();
    }
}
