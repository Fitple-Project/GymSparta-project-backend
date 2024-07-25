package com.sparta.fltpleprojectbackend.review.service;

import com.sparta.fltpleprojectbackend.enums.ErrorType;
import com.sparta.fltpleprojectbackend.review.dto.ReviewRequest;
import com.sparta.fltpleprojectbackend.review.dto.ReviewResponse;
import com.sparta.fltpleprojectbackend.review.entity.Review;
import com.sparta.fltpleprojectbackend.review.exception.ReviewException;
import com.sparta.fltpleprojectbackend.review.repository.ReviewRepository;
import com.sparta.fltpleprojectbackend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;


    /**
     * 리뷰 생성
     *
     * @param reviewRequest 리뷰 생성 요청 데이터. 사용자의 리뷰 내용을 포함합니다.
     * @param ordersId 주문 ID. 리뷰를 작성할 주문의 고유 식별자입니다.
     * @return 생성된 리뷰에 대한 응답 데이터.
     */
    public ReviewResponse createReview(ReviewRequest reviewRequest,  Long ordersId
                                       //        , User user
    ) {
        // 주문에서 상품 구매 내역에 따라서 리뷰 작성
        //  - 상품 구매 내역이 없는 경우 리뷰 작성 불가(예외 처리)
        // 매장 테이블 밑에 리뷰 테이블을 만들어서 리뷰를 생성
        Review review = reviewRepository.save(new Review(reviewRequest
                //        , user
        ));
        return new ReviewResponse(review);
    }

    /**
     * 주문 ID에 해당하는 모든 리뷰 조회
     *
     * @param ordersId 주문 ID. 조회할 리뷰가 속한 주문의 고유 식별자입니다.
     * @return 조회된 리뷰 목록에 대한 응답 데이터.
     */
    /*
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long ordersId) {
        List<Review> reviews = reviewRepository.findByOrdersId(ordersId);
        return reviews.stream()
                .map(review -> new ReviewResponse(review))
                .collect(Collectors.toList());
    }

     */

    /**
     * 리뷰 수정
     *
     * @param reviewId 리뷰 ID. 수정할 리뷰의 고유 식별자입니다.
     * @param reviewRequest 리뷰 수정 요청 데이터. 수정할 리뷰의 내용을 포함합니다.
     * @return 수정된 리뷰에 대한 응답 데이터.
     * @throws IllegalArgumentException 리뷰가 존재하지 않거나 수정 가능 기간이 지난 경우 예외를 발생시킵니다.
     */
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest
                                       //        , User user
    ) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ReviewException(ErrorType.REVIEW_NOT_FOUND)
        );

        if(review.getCreatedAt().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new ReviewException(ErrorType.REVIEW_MODIFICATION_PERIOD_EXPIRED);
        }

        /*
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("리뷰 작성자만 수정 가능합니다.");
        }
         */
        review.update(reviewRequest);

        return new ReviewResponse(review);
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId 리뷰 ID. 삭제할 리뷰의 고유 식별자입니다.
     * @throws IllegalArgumentException 리뷰가 존재하지 않는 경우 예외를 발생시킵니다.
     */
    @Transactional
    public void deleteReview(Long reviewId
                             //        , User user
    ) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ReviewException(ErrorType.REVIEW_NOT_FOUND)
        );

        /*
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("리뷰 작성자만 삭제 가능합니다.");
        }
         */

        reviewRepository.delete(review);
    }
}
