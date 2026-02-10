package dev.eduteam.eduquest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import dev.eduteam.eduquest.services.questionari.CompitinoService;

import dev.eduteam.eduquest.models.accounts.Docente;
import dev.eduteam.eduquest.models.accounts.Studente;
import dev.eduteam.eduquest.models.questionari.Compilazione;
import dev.eduteam.eduquest.models.questionari.Domanda;
import dev.eduteam.eduquest.models.questionari.Questionario;
import dev.eduteam.eduquest.models.questionari.Risposta;
import dev.eduteam.eduquest.repositories.accounts.StudenteRepository;
import dev.eduteam.eduquest.repositories.questionari.CompilazioneRepository;
import dev.eduteam.eduquest.repositories.questionari.QuestionarioRepository;
import dev.eduteam.eduquest.repositories.questionari.RispostaRepository;
import dev.eduteam.eduquest.services.questionari.CompilazioneService;

class CompilazioneServiceTest {

        @Mock
        private CompilazioneRepository compilazioneRepository;

        @Mock
        private StudenteRepository studenteRepository;

        @Mock
        private QuestionarioRepository questionarioRepository;

        @Mock
        private RispostaRepository rispostaRepository;

        @Mock
        private CompitinoService compitinoService;

        @Mock
        private dev.eduteam.eduquest.services.questionari.DomandaService domandaService;

        @InjectMocks
        private CompilazioneService compilazioneService;

        private Compilazione compilazione;
        private Studente studente;
        private Questionario questionario;
        private Risposta risposta;
        private Docente docente;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);

                studente = new Studente("Mario", "Rossi", "mrossi", "mario@email.com", "password123");
                studente.setAccountID(1);

                docente = new Docente("Luigi", "Bianchi", "lbianchi", "luigi@email.com", "password456");

                ArrayList<Domanda> domande = new ArrayList<>();
                // Aggiungi 3 domande fittizie per testare
                Domanda d1 = mock(Domanda.class);
                Domanda d2 = mock(Domanda.class);
                Domanda d3 = mock(Domanda.class);
                when(d1.getPunteggio()).thenReturn(3);
                when(d2.getPunteggio()).thenReturn(3);
                when(d3.getPunteggio()).thenReturn(4);
                domande.add(d1);
                domande.add(d2);
                domande.add(d3);
                questionario = new Questionario("Test Questionario", "Descrizione", domande, docente,
                                Questionario.Difficulty.Facile);
                questionario.setID(1);

                compilazione = new Compilazione(studente, questionario);
                compilazione.setID(1);

                risposta = new Risposta("");
                risposta.setID(1);
        }

        @Test
        void testCreaCompilazioneSuccess() {
                when(studenteRepository.getStudenteByAccountID(1))
                                .thenReturn(studente);
                when(questionarioRepository.getQuestionarioByID(1))
                                .thenReturn(questionario);
                when(compitinoService.isCompilabileByStudente(1, 1)).thenReturn(true);
                when(compilazioneRepository.insertCompilazione(any(Compilazione.class)))
                                .thenReturn(compilazione);

                Compilazione risultato = compilazioneService.creaCompilazione(1, 1);

                assertNotNull(risultato);
                assertEquals(studente, risultato.getStudente());
                assertEquals(questionario, risultato.getQuestionario());
                verify(compilazioneRepository, times(1)).insertCompilazione(any(Compilazione.class));
        }

        @Test
        void testInserisciRispostaCompSuccess() {
                Risposta[] risposte = new Risposta[3];

                ArrayList<Risposta> risposteValide = new ArrayList<>();
                risposteValide.add(risposta);

                Domanda domanda = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
                domanda.setID(1);
                domanda.setPunteggio(5);

                when(compilazioneRepository.getCompilazioneByID(1))
                                .thenReturn(compilazione);
                when(compilazioneRepository.getRisposteCompilazione(1, 3))
                                .thenReturn(risposte);
                when(rispostaRepository.getRispostaByID(1))
                                .thenReturn(risposta);
                when(rispostaRepository.getRisposteByDomanda(1))
                                .thenReturn(risposteValide);
                when(compilazioneRepository.salvaRisposta(1, 1))
                                .thenReturn(true);
                when(compilazioneRepository.aggiornaPunteggio(anyInt(), anyInt()))
                                .thenReturn(true);
                when(domandaService.getDomandaByIdCompleta(1, 1))
                                .thenReturn(domanda);

                boolean risultato = compilazioneService.inserisciRispostaComp(1, 1, 1);

                assertTrue(risultato);
                verify(compilazioneRepository, times(1)).salvaRisposta(1, 1);
        }

        @Test
        void testInserisciRispostaCompRispostaInvalida() {
                Domanda domanda = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
                domanda.setID(1);
                domanda.setPunteggio(5);

                when(compilazioneRepository.getCompilazioneByID(1))
                                .thenReturn(compilazione);
                when(compilazioneRepository.getRisposteCompilazione(1, 3))
                                .thenReturn(new Risposta[3]);
                when(rispostaRepository.getRispostaByID(1))
                                .thenReturn(risposta);
                when(rispostaRepository.getRisposteByDomanda(1))
                                .thenReturn(new ArrayList<>());
                when(domandaService.getDomandaByIdCompleta(1, 1))
                                .thenReturn(domanda);

                boolean risultato = compilazioneService.inserisciRispostaComp(1, 1, 1);

                assertFalse(risultato);
                verify(compilazioneRepository, never()).salvaRisposta(anyInt(), anyInt());
        }

        @Test
        void testInserisciRispostaCompSalvataggioFallito() {
                Risposta[] risposte = new Risposta[3];

                ArrayList<Risposta> risposteValide = new ArrayList<>();
                risposteValide.add(risposta);

                Domanda domanda = Domanda.createDomandaOfType(Domanda.Type.DOMANDA_MULTIPLA);
                domanda.setID(1);
                domanda.setPunteggio(5);

                when(compilazioneRepository.getCompilazioneByID(1))
                                .thenReturn(compilazione);
                when(compilazioneRepository.getRisposteCompilazione(1, 3))
                                .thenReturn(risposte);
                when(rispostaRepository.getRispostaByID(1))
                                .thenReturn(risposta);
                when(rispostaRepository.getRisposteByDomanda(1))
                                .thenReturn(risposteValide);
                when(compilazioneRepository.salvaRisposta(1, 1))
                                .thenReturn(false);
                when(domandaService.getDomandaByIdCompleta(1, 1))
                                .thenReturn(domanda);

                boolean risultato = compilazioneService.inserisciRispostaComp(1, 1, 1);

                assertFalse(risultato);
        }

        @Test
        void testGetCompilazioniCompletate() {
                ArrayList<Compilazione> compilazioni = new ArrayList<>();
                compilazioni.add(compilazione);

                when(compilazioneRepository.getCompilazioniStatus(1, true))
                                .thenReturn(compilazioni);

                ArrayList<Compilazione> risultato = compilazioneService.getCompilazioniCompletate(1);

                assertNotNull(risultato);
                assertEquals(1, risultato.size());
                assertEquals(compilazione, risultato.get(0));
        }

        @Test
        void testGetCompilazioniCompletateEmpty() {
                when(compilazioneRepository.getCompilazioniStatus(1, true))
                                .thenReturn(new ArrayList<>());

                ArrayList<Compilazione> risultato = compilazioneService.getCompilazioniCompletate(1);

                assertNotNull(risultato);
                assertTrue(risultato.isEmpty());
        }

        @Test
        void testChiudiCompilazioneSuccess() {
                // preparazione: compilazione con punteggio e una precedente compilazione
                // completata
                compilazione.setPunteggio(2);
                ArrayList<Compilazione> compilazioni = new ArrayList<>();
                compilazioni.add(new Compilazione(studente, questionario));

                when(compilazioneRepository.getCompilazioneByID(1))
                                .thenReturn(compilazione);
                when(studenteRepository.getStudenteByAccountID(1))
                                .thenReturn(studente);
                when(questionarioRepository.getQuestionarioByID(1))
                                .thenReturn(questionario);
                when(compilazioneRepository.getCompilazioniStatus(1, true))
                                .thenReturn(compilazioni);
                when(studenteRepository.updateStudente(any(Studente.class)))
                                .thenReturn(true);
                when(compilazioneRepository.upateStatusCompilazione(1, true))
                                .thenReturn(true);

                boolean risultato = compilazioneService.chiudiCompilazione(1, 1);

                assertTrue(risultato);
                // votoNormalizzato = (2/10)*100 = 20; nuova media: (0*1 + 20) / (1+1) = 10.0
                assertEquals(10.0, studente.getMediaPunteggio(), 0.001);
                verify(studenteRepository, times(1)).updateStudente(any(Studente.class));
                verify(compilazioneRepository, times(1)).upateStatusCompilazione(1, true);
        }

        @Test
        void testChiudiCompilazioneAggiornaMediaConMediaPreesistente() {
                // Studente con media già presente e due compilazioni già completate
                compilazione.setPunteggio(8); // punteggio ottenuto nella compilazione corrente
                studente.setMediaPunteggio(70.0); // media già esistente

                ArrayList<Compilazione> compilazioni = new ArrayList<>();
                compilazioni.add(new Compilazione(studente, questionario));
                compilazioni.add(new Compilazione(studente, questionario));

                when(compilazioneRepository.getCompilazioneByID(1)).thenReturn(compilazione);
                when(studenteRepository.getStudenteByAccountID(1)).thenReturn(studente);
                when(questionarioRepository.getQuestionarioByID(1)).thenReturn(questionario);
                when(compilazioneRepository.getCompilazioniStatus(1, true)).thenReturn(compilazioni);
                when(studenteRepository.updateStudente(any(Studente.class))).thenReturn(true);
                when(compilazioneRepository.upateStatusCompilazione(1, true)).thenReturn(true);

                boolean risultato = compilazioneService.chiudiCompilazione(1, 1);

                assertTrue(risultato);
                // Calcolo atteso: votoNormalizzato = (8/10)*100 = 80
                // nuovaMedia = ((70 * 2) + 80) / (2 + 1) = 220/3 = 73.333...
                assertEquals(73.33333333333333, studente.getMediaPunteggio(), 0.001);
                verify(studenteRepository, times(1)).updateStudente(any(Studente.class));
                verify(compilazioneRepository, times(1)).upateStatusCompilazione(1, true);
        }

        @Test
        void testChiudiCompilazioneFailureWhenUpdateFails() {
                compilazione.setPunteggio(3);
                ArrayList<Compilazione> compilazioni = new ArrayList<>();
                compilazioni.add(new Compilazione(studente, questionario));

                when(compilazioneRepository.getCompilazioneByID(1))
                                .thenReturn(compilazione);
                when(studenteRepository.getStudenteByAccountID(1))
                                .thenReturn(studente);
                when(questionarioRepository.getQuestionarioByID(1))
                                .thenReturn(questionario);
                when(compilazioneRepository.getCompilazioniStatus(1, true))
                                .thenReturn(compilazioni);
                when(studenteRepository.updateStudente(any(Studente.class)))
                                .thenReturn(true);
                when(compilazioneRepository.upateStatusCompilazione(1, true))
                                .thenReturn(false);

                boolean risultato = compilazioneService.chiudiCompilazione(1, 1);
                assertFalse(risultato);
        }

        @Test
        void testRiprendiCompilazioneSuccess() {
                Risposta[] risposte = new Risposta[3];
                risposte[0] = risposta;

                when(compilazioneRepository.getCompilazioneInSospeso(1, 1))
                                .thenReturn(compilazione);
                when(compilazioneRepository.getRisposteCompilazione(1, 3))
                                .thenReturn(risposte);

                Compilazione risultato = compilazioneService.riprendiCompilazione(1, 1);

                assertNotNull(risultato);
                assertNotNull(risultato.getRisposte());
                assertEquals(risposta, risultato.getRisposte()[0]);
                verify(compilazioneRepository, times(1)).getCompilazioneInSospeso(1, 1);
                verify(compilazioneRepository, times(1)).getRisposteCompilazione(1, 3);
        }

        @Test
        void testRiprendiCompilazioneReturnsNullWhenNoInCorso() {
                when(compilazioneRepository.getCompilazioneInSospeso(1, 1))
                                .thenReturn(null);

                Compilazione risultato = compilazioneService.riprendiCompilazione(1, 1);

                assertNull(risultato);
                verify(compilazioneRepository, times(1)).getCompilazioneInSospeso(1, 1);
                verify(compilazioneRepository, never()).getRisposteCompilazione(anyInt(), anyInt());
        }
}
