package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;

@ExtendWith(MockitoExtension.class)
public class ClassificaServiceTest {

    @Mock
    private StudenteRepository studenteRepository;

    @Mock
    private CompilazioneRepository compilazioneRepository;

    @InjectMocks
    private ClassificaService classificaService;

    private List<Studente> mockStudenti;
    private List<Compilazione> mockCompilazioni;

    @BeforeEach
    void setUp() {
        mockStudenti = new ArrayList<>();
        mockStudenti.add(new Studente("Mario", "Rossi", "mario", "m@r.it", "pw"));
        mockCompilazioni = new ArrayList<>();
        mockCompilazioni.add(mock(Compilazione.class));
    }

    @Test
    void getClassificaEduPoints_Success() {
        when(studenteRepository.getTopStudentiPerEduPoints(10)).thenReturn((ArrayList<Studente>) mockStudenti);

        List<Studente> result = classificaService.getClassificaEduPoints(10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studenteRepository).getTopStudentiPerEduPoints(10);
    }

    @Test
    void getClassificaMedia_Success() {
        when(studenteRepository.getTopStudentiPerMedia(5)).thenReturn((ArrayList<Studente>) mockStudenti);

        List<Studente> result = classificaService.getClassificaMedia(5);

        assertEquals(1, result.size());
        verify(studenteRepository).getTopStudentiPerMedia(5);
    }

    @Test
    void getClassificaCompilazioniGlobale_Success() {
        when(compilazioneRepository.getTopCompilazioniGlobale(3))
                .thenReturn((ArrayList<Compilazione>) mockCompilazioni);

        List<Compilazione> result = classificaService.getClassificaCompilazioniGlobale(3);

        assertFalse(result.isEmpty());
        verify(compilazioneRepository).getTopCompilazioniGlobale(3);
    }

    @Test
    void getClassificaPerQuestionario_Success() {
        int questionarioID = 1;
        int limite = 10;
        when(compilazioneRepository.getTopCompilazioniPerQuestionario(questionarioID, limite))
                .thenReturn((ArrayList<Compilazione>) mockCompilazioni);

        List<Compilazione> result = classificaService.getClassificaPerQuestionario(questionarioID, limite);

        assertNotNull(result);
        verify(compilazioneRepository).getTopCompilazioniPerQuestionario(questionarioID, limite);
    }
}
