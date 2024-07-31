package com.sparta.fltpleprojectbackend.store.service;

import com.sparta.fltpleprojectbackend.enums.ErrorType;
import com.sparta.fltpleprojectbackend.owner.entity.Owner;
import com.sparta.fltpleprojectbackend.security.UserDetailsImpl;
import com.sparta.fltpleprojectbackend.store.dto.StoreRequest;
import com.sparta.fltpleprojectbackend.store.dto.StoreResponse;
import com.sparta.fltpleprojectbackend.store.dto.StoreSimpleResponse;
import com.sparta.fltpleprojectbackend.store.entity.Store;
import com.sparta.fltpleprojectbackend.store.exception.StoreException;
import com.sparta.fltpleprojectbackend.store.repository.StoreRepository;
import com.sparta.fltpleprojectbackend.trainer.exception.TrainerException;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매장 관련 작업을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;

  /**
   * 1. 매장 등록
   *
   * @param request 등록할 매장의 세부 정보
   * @return 등록된 매장의 세부 정보를 포함하는 응답 객체
   */
  public StoreResponse createStore(StoreRequest request, Owner owner) {
    if (owner == null) {
      throw new StoreException(ErrorType.FORBIDDEN_OPERATION);
    }
    Store store = new Store(request, owner);
    storeRepository.save(store);
    return new StoreResponse(store);
  }

  /**
   * 2. 매장 정보 수정
   *
   * @param storeId 수정하려는 매장의 id 값
   * @param request 수정할 매장의 세부 정보
   * @return 수정된 매장의 세부 정보를 포함하는 응답 객체
   */
  @Transactional
  public StoreResponse updateStore(Long storeId, StoreRequest request, String accountId) {
    Store store = findStoreById(storeId);

    validateUser(store, accountId);

    store.update(request);
    return new StoreResponse(store);
  }

  /**
   * 3. 매장 삭제
   *
   * @param storeId 삭제하려는 매장의 id 값
   */
  public void deleteStore(Long storeId, String accountId) {
    Store store = findStoreById(storeId);

    validateUser(store, accountId);

    storeRepository.delete(store);
  }

  /**
   * 4. 매장 전체 조회
   *
   * @return 등록된 모든 매장의 ID와 매장 명
   */
  public List<StoreSimpleResponse> findAll() {
    List<Store> storeList = storeRepository.findAll();
    return storeList.stream()
        .sorted(Comparator.comparing(Store::getCreatedAt))
        .map(StoreSimpleResponse::new)
        .toList();
  }

  /**
   * 5. 매장 상세 조회
   *
   * @param storeId 상세 조회하고자하는 매장 ID
   * @return 상세 조회한 매장의 상세 정보
   */
  public StoreResponse findById(Long storeId) {
    Store store = findStoreById(storeId);

    return new StoreResponse(store);
  }

  /**
   * 6. 점주가 등록한 매장 전체 조회
   *
   * @param accountId 점주의 ID
   * @return 점주가 등록한 모든 매장의 ID와 매장 명
   */
  public List<StoreSimpleResponse> findAllAdmin(String accountId) {
    List<Store> storeList = storeRepository.findAllByOwnerAccountId(accountId);
    return storeList.stream()
        .sorted(Comparator.comparing(Store::getCreatedAt))
        .map(StoreSimpleResponse::new)
        .toList();
  }

  /**
   * 7. 점주가 등록한 매장의 상세 조회
   *
   * @param accountId 점주의 ID
   * @param storeId   조회하고자하는 매장의 ID
   * @return 조회한 매장의 상세 정보
   */
  public StoreResponse findAdminById(String accountId, Long storeId) {
    Store store = findStoreById(storeId);

    validateUser(store, accountId);

    return new StoreResponse(store);
  }

  private Store findStoreById(long id) {
    return storeRepository.findById(id)
        .orElseThrow(() -> new StoreException(ErrorType.NOT_FOUND_STORE));
  }

  private void validateUser(Store store, String accountId) {
    if (!store.getOwner().getAccountId().equals(accountId)) {
      throw new StoreException(ErrorType.INVALID_USER);
    }
  }
}