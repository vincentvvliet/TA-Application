package nl.tudelft.sem.TAs.services;

import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkingHoursService {
    @Autowired
    private ContractRepository contractRepository;

    public Contract checkContract(TA ta, int hours) throws ResponseStatusException {
        if (hours <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "number of hours must be positive");
        }
        Contract contract = contractRepository.findByStudentIdAndCourseId(ta.getStudentId(), ta.getCourseId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TA does not have a contract"));
        if (hours > contract.getMaxHours()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "number of hours must not exceed hours on contract");
        }
        return contract;
    }
}
