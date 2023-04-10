//package client.scenes;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import client.utils.ServerUtils;
//import commons.Card;
//import commons.Tag;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//
//@RunWith(MockitoJUnitRunner.class)
//class CardPopupCtrlTest {
//    @Mock
//    private MainCtrlTalio mainCtrlTalio;
//    @Mock
//    private ServerUtils serverUtils;
//    private CardPopupCtrl cardPopupCtrl;
//    private Card card;
//    private Set<Tag> originalTags;
//    private List<Tag> cardTags;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        card = new Card();
//        card.setId(123L);
//        cardTags = new ArrayList<>();
//        Tag tag1 = new Tag();
//        tag1.setId(1L);
//        tag1.setTitle("Tag 1");
//        Tag tag2 = new Tag();
//        tag2.setId(2L);
//        tag2.setTitle("Tag 2");
//        cardTags.add(tag1);
//        cardTags.add(tag2);
//        card.setTags(cardTags);
//        originalTags = new HashSet<>();
//        originalTags.add(tag1);
//        originalTags.add(tag2);
//
//        MockitoAnnotations.openMocks(this);
//        card.setTitle("Test Card");
//        cardPopupCtrl = new CardPopupCtrl(mainCtrlTalio, card, serverUtils);
//
//
//
//           }
//
//    @Test
//    public void testSave() {
//        when(serverUtils.getCard(card.getId())).thenReturn(card);
//
//
//        cardPopupCtrl.save();
//        //checking if the serverUtil
//        verify(serverUtils).updateCard(card);
//
//        Set<Tag> tagsToRemove = new HashSet<>(originalTags);
//        tagsToRemove.removeAll(cardTags);
//        assertEquals(tagsToRemove, cardPopupCtrl.getCardTags());
//
//    }
//}
