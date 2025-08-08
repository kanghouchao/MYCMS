<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('shops', function (Blueprint $table) {
            // 使用短随机字符串作为主键（应用层负责生成）
            $table->string('id')->primary();
            $table->json('data')->nullable(); // 动态属性：name, email, plan, etc.
            $table->timestamps();

            // 常用查询字段的冗余索引（JSON 内部可按需追加）
            // 如果后续访问量较大，可考虑 materialize name/email 到独立列再建索引
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('shops');
    }
};
