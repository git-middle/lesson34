package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 日報新規登録（保存）
    @Transactional
    public ErrorKinds save(Report report) {

        //日付重複チェック
       if (findByEmployeeAndReportDate(report.getEmployee().getCode(),report.getReportDate()) != null) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    private Report findByEmployeeAndReportDate(String code,LocalDate reportDate) {
        return reportRepository.findByEmployeeCodeAndReportDate(code, reportDate);
    }

    public ErrorKinds existsByIdAndLocalDate(Report report,Employee code,LocalDate report_date) {
        Integer check = reportRepository.countByEmployeeAndReportDate(code,report_date);

        if (check >= 1) {
            return ErrorKinds.DATECHECK_ERROR;
        } else {

            return ErrorKinds.SUCCESS;
        }

    }


    // 日報更新
    @Transactional
    public ErrorKinds update(Report report,UserDetail userDetail) {

        Report oldReport = reportRepository.findById(report.getId()).orElse(null);
        report.setDeleteFlg(false);
        report.setEmployee(oldReport.getEmployee());

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        if (!oldReport.getReportDate().equals(report.getReportDate())) {
            if (findByEmployeeAndReportDate(report.getEmployee().getCode(), report.getReportDate()) != null) {
                return ErrorKinds.DATECHECK_ERROR;
            }
        }

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {

    /*
        // 自分を削除しようとした場合はエラーメッセージを表示
       if (code.equals(userDetail.getReport().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }
     */
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(Integer id) {

        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    //従業員情報から日報を検索
    public List<Report> findByEmployee(Employee employee) {

        return reportRepository.findByEmployee(employee);
    }

}
