package goorm.goormspring1.post;

import goorm.goormspring1.post.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service  // Spring Service Bean으로 등록
@RequiredArgsConstructor  // Lombok: final 필드에 대한 생성자 자동 생성
public class PostService {

    private final PostRepository postRepository;  // 의존성 주입

    // 전체 게시글 조회
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // 페이징 전체 조회 (Page 반환)
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    // SEQ로 게시글 조회
    public Post findBySeq(Long seq) {
        return postRepository.findById(seq)
                .orElseThrow(() -> new PostNotFoundException(seq));
    }

    // 게시글 저장
    @Transactional  // 쓰기 작업은 별도 트랜잭션
    public Post save(Post post) {
        return postRepository.save(post);
    }

    // 게시글 수정
    @Transactional
    public Post update(Long seq, Post updatePost) {
        Post post = findBySeq(seq);
        post.setTitle(updatePost.getTitle());
        post.setContent(updatePost.getContent());
//        post.setAuthor(updatePost.getAuthor());
        return post;  // @Transactional에 의해 자동으로 UPDATE 쿼리 실행
    }

    // 게시글 삭제
    @Transactional
    public void delete(Long seq) {
        postRepository.findById(seq).orElseThrow(()->new PostNotFoundException(seq));
        postRepository.deleteById(seq);
    }
}
