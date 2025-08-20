<?php

namespace App\Jobs;

use App\Exceptions\ServiceException;
use App\Models\Central\Tenant;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Redis;

class SendRegisterMailJob
{
    private string $tenantId;
    private string $email;
    private string $domain;

    public $tries = 1;

    public function __construct(Tenant $tenant)
    {
        $this->tenantId = $tenant->id;
        $this->email = $tenant->email;
        $domainObj = $tenant->domains()->first();
        $this->domain = $domainObj ? $domainObj->domain : '';
    }

    /**
     * メール本文生成
     */
    private function generateMailText(string $url): string
    {
        return "【Oli-CMS】管理者登録のご案内\n\n" .
            "貴社のOli-CMSテナントが正常に作成されました。\n" .
            "下記のリンクより管理者アカウントの登録を完了してください。\n\n" .
            "登録用リンク: {$url}\n\n" .
            "※本リンクは1時間後に無効となりますので、お早めにご登録ください。\n" .
            "ご不明点がございましたら、サポートまでご連絡ください。\n\n" .
            "Oli-CMS運営事務局";
    }

    public function handle()
    {
        if (!$this->domain || !$this->email) {
            throw new ServiceException('テナントのドメインまたはメールアドレスが存在しないため、登録案内メールを送信できません');
        }

        $token = Str::random(60);
        $url = "http://{$this->domain}/tenant/register?token={$token}";
        $mailText = $this->generateMailText($url);

        $redisKey = "tenant_register_token:{$this->tenantId}";

        try {
            Mail::raw($mailText, function ($message) {
                $message->to($this->email)->subject('【Oli-CMS】管理者登録のご案内');
            });
            Redis::setex($redisKey, 3600, $token);
        } catch (\Throwable $e) {
            Log::error('管理者登録メール送信に失敗しました', [
                '例外' => $e,
                'テナントID' => $this->tenantId,
                'メールアドレス' => $this->email
            ]);
            throw $e;
        }
    }
}
