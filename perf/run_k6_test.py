import subprocess
import sys
import os

# 아래 테스트트 N 명의 가상 사용자가 k초동안 동시에 사용하는 시나리오 1개만 상정

# 테스트할 서버 URL 목록
TARGET_URLS = [
    "http://localhost:8080"
    #"http://192.168.0.10:8080",
    #"https://your-prod-api.com"
]

# JS 템플릿
K6_TEMPLATE = """
import {{ check }} from "k6";
import http from "k6/http";

export default function() {{

    let res1 = http.get("{base}/api/dashboard/summary");
    check(res1, {{
        "summary status 200": (r) => r.status === 200,
    }});

    let res2 = http.get("{base}/api/dashboard/timeseries");
    check(res2, {{
        "timeseries status 200": (r) => r.status === 200,
    }});

    let res3 = http.get("{base}/api/dashboard/material-breakdown");
    check(res3, {{
        "material-breakdown status 200": (r) => r.status === 200,
    }});
}}
"""


def write_k6_script(base_url, file_name):
    with open(file_name, "w") as f:
        script = K6_TEMPLATE.format(base=base_url)
        f.write(script)


def run_k6(script_path):
    # cmd configuration
    vu = "1000"
    latency = "30s"

    cmd = ["k6", "run", "-u", vu, "-d", latency, script_path]
    print(f"\n=== Running: {' '.join(cmd)} ===\n")

    # 실행
    subprocess.run(cmd)


def main():
    for url in TARGET_URLS:
        # URL 기준 js 파일 만들기
        #safe_name = url.replace("http://", "").replace("https://", "").replace("/", "_")
        js_file = "k6_test.js"

        print(f"[+] Generating JS for {url} → {js_file}")
        write_k6_script(url, js_file)

        # k6 실행
        run_k6(js_file)


if __name__ == "__main__":
    main()
