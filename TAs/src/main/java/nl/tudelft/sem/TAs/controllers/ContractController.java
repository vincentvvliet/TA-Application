package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contract/")
public class ContractController {
    @Autowired
    private ContractRepository contractRepository;

    @GetMapping("/getContract/{id}")
    public Optional<Contract> getContractById(@PathVariable (value = "id") UUID id) {
        return contractRepository.findById(id);
    }

    @GetMapping("/getContracts")
    public List<Contract> getContracts() {
        return contractRepository.findAll();
    }

    @PostMapping("/createContract")
    public boolean createContract() {
        Contract c = new Contract();
        contractRepository.save(c);
        return true;
    }
}
