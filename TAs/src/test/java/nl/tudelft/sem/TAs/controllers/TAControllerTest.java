package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.TARepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TAControllerTest {

    @InjectMocks
    TAController taController;

    @Mock
    TARepository taRepository;

    TA ta = new TA();
    List<TA> list = new ArrayList<>();
    @BeforeEach
    public void init() {
        list.add(ta);
        when(taRepository.findById(ta.getId())).thenReturn(Optional.ofNullable(ta));
    }

    @Test
    public void findByIdTest() {
        Assertions.assertEquals(taController.getTAById(ta.getId()), Optional.ofNullable(ta));
    }

    @Test
    public void findAllTest() {
        when(taRepository.findAll()).thenReturn(list);
        Assertions.assertEquals(taController.getTAs(),list);
    }

}
